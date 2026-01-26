package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import com.carlosnicolaugalves.makelifebetter.model.Question
import com.carlosnicolaugalves.makelifebetter.model.QuestionReply
import kotlinx.coroutines.Job

class SharedChatViewModelWrapper {
    private val viewModel = SharedChatViewModel()

    private var chatStateObserver: Job? = null
    private var messagesObserver: Job? = null
    private var sendMessageObserver: Job? = null
    private var questionsStateObserver: Job? = null
    private var questionsObserver: Job? = null
    private var addQuestionObserver: Job? = null
    private var repliesStateObserver: Job? = null
    private var repliesObserver: Job? = null
    private var addReplyObserver: Job? = null
    private var selectedQuestionObserver: Job? = null

    // MARK: - Chat Messages

    fun observeChatState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (List<ChatMessage>) -> Unit,
        onError: (String) -> Unit
    ) {
        chatStateObserver?.cancel()
        chatStateObserver = viewModel.observeChatState { state ->
            when (state) {
                is ChatState.Idle -> onIdle()
                is ChatState.Loading -> onLoading()
                is ChatState.Success -> onSuccess(state.messages)
                is ChatState.Error -> onError(state.message)
            }
        }
    }

    fun observeMessages(callback: (List<ChatMessage>) -> Unit) {
        messagesObserver?.cancel()
        messagesObserver = viewModel.observeMessages(callback)
    }

    fun observeSendMessageState(
        onIdle: () -> Unit,
        onSending: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        sendMessageObserver?.cancel()
        sendMessageObserver = viewModel.observeSendMessageState { state ->
            when (state) {
                is SendMessageState.Idle -> onIdle()
                is SendMessageState.Sending -> onSending()
                is SendMessageState.Success -> onSuccess()
                is SendMessageState.Error -> onError(state.message)
            }
        }
    }

    fun sendMessage(author: String, message: String) {
        viewModel.sendMessage(author, message)
    }

    fun refreshMessages() {
        viewModel.refreshMessages()
    }

    // MARK: - Questions

    fun observeQuestionsState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (List<Question>) -> Unit,
        onError: (String) -> Unit
    ) {
        questionsStateObserver?.cancel()
        questionsStateObserver = viewModel.observeQuestionsState { state ->
            when (state) {
                is QuestionsState.Idle -> onIdle()
                is QuestionsState.Loading -> onLoading()
                is QuestionsState.Success -> onSuccess(state.questions)
                is QuestionsState.Error -> onError(state.message)
            }
        }
    }

    fun observeQuestions(callback: (List<Question>) -> Unit) {
        questionsObserver?.cancel()
        questionsObserver = viewModel.observeQuestions(callback)
    }

    fun observeAddQuestionState(
        onIdle: () -> Unit,
        onAdding: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        addQuestionObserver?.cancel()
        addQuestionObserver = viewModel.observeAddQuestionState { state ->
            when (state) {
                is AddQuestionState.Idle -> onIdle()
                is AddQuestionState.Adding -> onAdding()
                is AddQuestionState.Success -> onSuccess()
                is AddQuestionState.Error -> onError(state.message)
            }
        }
    }

    fun addQuestion(author: String, title: String, description: String) {
        viewModel.addQuestion(author, title, description)
    }

    fun deleteQuestion(questionId: String) {
        viewModel.deleteQuestion(questionId)
    }

    fun refreshQuestions() {
        viewModel.refreshQuestions()
    }

    // MARK: - Replies

    fun observeRepliesState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (List<QuestionReply>) -> Unit,
        onError: (String) -> Unit
    ) {
        repliesStateObserver?.cancel()
        repliesStateObserver = viewModel.observeRepliesState { state ->
            when (state) {
                is RepliesState.Idle -> onIdle()
                is RepliesState.Loading -> onLoading()
                is RepliesState.Success -> onSuccess(state.replies)
                is RepliesState.Error -> onError(state.message)
            }
        }
    }

    fun observeReplies(callback: (List<QuestionReply>) -> Unit) {
        repliesObserver?.cancel()
        repliesObserver = viewModel.observeReplies(callback)
    }

    fun observeAddReplyState(
        onIdle: () -> Unit,
        onAdding: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        addReplyObserver?.cancel()
        addReplyObserver = viewModel.observeAddReplyState { state ->
            when (state) {
                is AddReplyState.Idle -> onIdle()
                is AddReplyState.Adding -> onAdding()
                is AddReplyState.Success -> onSuccess()
                is AddReplyState.Error -> onError(state.message)
            }
        }
    }

    fun observeSelectedQuestion(callback: (Question?) -> Unit) {
        selectedQuestionObserver?.cancel()
        selectedQuestionObserver = viewModel.observeSelectedQuestion(callback)
    }

    fun selectQuestion(question: Question) {
        viewModel.selectQuestion(question)
    }

    fun clearSelectedQuestion() {
        viewModel.clearSelectedQuestion()
    }

    fun addReply(questionId: String, author: String, content: String) {
        viewModel.addReply(questionId, author, content)
    }

    fun deleteReply(questionId: String, replyId: String) {
        viewModel.deleteReply(questionId, replyId)
    }

    // MARK: - Cleanup

    fun clear() {
        chatStateObserver?.cancel()
        messagesObserver?.cancel()
        sendMessageObserver?.cancel()
        questionsStateObserver?.cancel()
        questionsObserver?.cancel()
        addQuestionObserver?.cancel()
        repliesStateObserver?.cancel()
        repliesObserver?.cancel()
        addReplyObserver?.cancel()
        selectedQuestionObserver?.cancel()
        viewModel.clear()
    }
}
