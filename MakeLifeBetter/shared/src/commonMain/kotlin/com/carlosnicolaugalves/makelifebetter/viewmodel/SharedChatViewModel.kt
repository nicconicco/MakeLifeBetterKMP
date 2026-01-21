package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import com.carlosnicolaugalves.makelifebetter.model.Question
import com.carlosnicolaugalves.makelifebetter.repository.GeneralChatRepository
import com.carlosnicolaugalves.makelifebetter.repository.QuestionRepository
import com.carlosnicolaugalves.makelifebetter.repository.createGeneralChatRepository
import com.carlosnicolaugalves.makelifebetter.repository.createQuestionRepository
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

sealed class QuestionsState {
    object Idle : QuestionsState()
    object Loading : QuestionsState()
    data class Success(val questions: List<Question>) : QuestionsState()
    data class Error(val message: String) : QuestionsState()
}

sealed class AddQuestionState {
    object Idle : AddQuestionState()
    object Adding : AddQuestionState()
    object Success : AddQuestionState()
    data class Error(val message: String) : AddQuestionState()
}

class SharedChatViewModel(
    private val chatRepository: GeneralChatRepository = createGeneralChatRepository(),
    private val questionRepository: QuestionRepository = createQuestionRepository()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Chat messages state
    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _sendMessageState = MutableStateFlow<SendMessageState>(SendMessageState.Idle)
    val sendMessageState: StateFlow<SendMessageState> = _sendMessageState.asStateFlow()

    // Questions state
    private val _questionsState = MutableStateFlow<QuestionsState>(QuestionsState.Idle)
    val questionsState: StateFlow<QuestionsState> = _questionsState.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _addQuestionState = MutableStateFlow<AddQuestionState>(AddQuestionState.Idle)
    val addQuestionState: StateFlow<AddQuestionState> = _addQuestionState.asStateFlow()

    init {
        loadMessages()
        loadQuestions()
    }

    // Chat messages functions
    fun loadMessages() {
        viewModelScope.launch {
            _chatState.value = ChatState.Loading

            chatRepository.getMessages()
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

            chatRepository.sendMessage(author, message)
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

    fun refreshMessages() {
        loadMessages()
    }

    fun resetSendMessageState() {
        _sendMessageState.value = SendMessageState.Idle
    }

    // Questions functions
    fun loadQuestions() {
        viewModelScope.launch {
            _questionsState.value = QuestionsState.Loading

            questionRepository.getQuestions()
                .onSuccess { questionList ->
                    _questions.value = questionList
                    _questionsState.value = QuestionsState.Success(questionList)
                }
                .onFailure { exception ->
                    _questionsState.value = QuestionsState.Error(exception.message ?: "Erro ao carregar perguntas")
                }
        }
    }

    fun addQuestion(author: String, title: String, description: String) {
        if (title.isBlank() || description.isBlank()) return

        viewModelScope.launch {
            _addQuestionState.value = AddQuestionState.Adding

            questionRepository.addQuestion(author, title, description)
                .onSuccess { question ->
                    _questions.value = listOf(question) + _questions.value
                    _addQuestionState.value = AddQuestionState.Success
                    _addQuestionState.value = AddQuestionState.Idle
                }
                .onFailure { exception ->
                    _addQuestionState.value = AddQuestionState.Error(exception.message ?: "Erro ao adicionar pergunta")
                }
        }
    }

    fun deleteQuestion(questionId: String) {
        viewModelScope.launch {
            questionRepository.deleteQuestion(questionId)
                .onSuccess {
                    _questions.value = _questions.value.filter { it.id != questionId }
                }
                .onFailure { exception ->
                    // Handle error if needed
                }
        }
    }

    fun refreshQuestions() {
        loadQuestions()
    }

    fun resetAddQuestionState() {
        _addQuestionState.value = AddQuestionState.Idle
    }
}
