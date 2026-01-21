package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import com.carlosnicolaugalves.makelifebetter.repository.GeneralChatRepository
import com.carlosnicolaugalves.makelifebetter.repository.createGeneralChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatState {
    object Idle : ChatState()
    object Loading : ChatState()
    data class Success(val messages: List<ChatMessage>) : ChatState()
    data class Error(val message: String) : ChatState()
}

sealed class SendMessageState {
    object Idle : SendMessageState()
    object Sending : SendMessageState()
    object Success : SendMessageState()
    data class Error(val message: String) : SendMessageState()
}

class SharedChatViewModel(
    private val repository: GeneralChatRepository = createGeneralChatRepository()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _sendMessageState = MutableStateFlow<SendMessageState>(SendMessageState.Idle)
    val sendMessageState: StateFlow<SendMessageState> = _sendMessageState.asStateFlow()

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            _chatState.value = ChatState.Loading

            repository.getMessages()
                .onSuccess { messageList ->
                    _messages.value = messageList
                    _chatState.value = ChatState.Success(messageList)
                }
                .onFailure { exception ->
                    _chatState.value = ChatState.Error(exception.message ?: "Erro ao carregar mensagens")
                }
        }
    }

    fun sendMessage(author: String, message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            _sendMessageState.value = SendMessageState.Sending

            repository.sendMessage(author, message)
                .onSuccess { chatMessage ->
                    _messages.value = _messages.value + chatMessage
                    _sendMessageState.value = SendMessageState.Success
                    _sendMessageState.value = SendMessageState.Idle
                }
                .onFailure { exception ->
                    _sendMessageState.value = SendMessageState.Error(exception.message ?: "Erro ao enviar mensagem")
                }
        }
    }

    fun refresh() {
        loadMessages()
    }

    fun resetSendMessageState() {
        _sendMessageState.value = SendMessageState.Idle
    }
}
