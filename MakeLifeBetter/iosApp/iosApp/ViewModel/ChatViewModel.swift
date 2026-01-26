import Foundation
import Observation
import ComposeApp

@Observable
class ChatViewModel {

    // Chat messages
    var messages: [ChatMessage] = []
    var isChatLoading: Bool = true
    var isSending: Bool = false

    // Questions
    var questions: [Question] = []
    var isQuestionsLoading: Bool = true
    var isAddingQuestion: Bool = false

    // Replies
    var selectedQuestion: Question? = nil
    var replies: [QuestionReply] = []
    var isRepliesLoading: Bool = false
    var isAddingReply: Bool = false

    private let sharedViewModel: SharedChatViewModelWrapper

    init() {
        sharedViewModel = SharedChatViewModelWrapper()
        setupObservers()
    }

    private func setupObservers() {
        // Chat state
        sharedViewModel.observeChatState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isChatLoading = false }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async { self?.isChatLoading = true }
            },
            onSuccess: { [weak self] _ in
                DispatchQueue.main.async { self?.isChatLoading = false }
            },
            onError: { [weak self] _ in
                DispatchQueue.main.async { self?.isChatLoading = false }
            }
        )

        // Messages list
        sharedViewModel.observeMessages { [weak self] kotlinMessages in
            DispatchQueue.main.async {
                self?.messages = kotlinMessages.map { ChatMessage(from: $0 as! ComposeApp.ChatMessage) }
            }
        }

        // Send message state
        sharedViewModel.observeSendMessageState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isSending = false }
            },
            onSending: { [weak self] in
                DispatchQueue.main.async { self?.isSending = true }
            },
            onSuccess: { [weak self] in
                DispatchQueue.main.async { self?.isSending = false }
            },
            onError: { [weak self] _ in
                DispatchQueue.main.async { self?.isSending = false }
            }
        )

        // Questions state
        sharedViewModel.observeQuestionsState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isQuestionsLoading = false }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async { self?.isQuestionsLoading = true }
            },
            onSuccess: { [weak self] _ in
                DispatchQueue.main.async { self?.isQuestionsLoading = false }
            },
            onError: { [weak self] _ in
                DispatchQueue.main.async { self?.isQuestionsLoading = false }
            }
        )

        // Questions list
        sharedViewModel.observeQuestions { [weak self] kotlinQuestions in
            DispatchQueue.main.async {
                self?.questions = kotlinQuestions.map { Question(from: $0 as! ComposeApp.Question) }
            }
        }

        // Add question state
        sharedViewModel.observeAddQuestionState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isAddingQuestion = false }
            },
            onAdding: { [weak self] in
                DispatchQueue.main.async { self?.isAddingQuestion = true }
            },
            onSuccess: { [weak self] in
                DispatchQueue.main.async { self?.isAddingQuestion = false }
            },
            onError: { [weak self] _ in
                DispatchQueue.main.async { self?.isAddingQuestion = false }
            }
        )

        // Replies state
        sharedViewModel.observeRepliesState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isRepliesLoading = false }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async { self?.isRepliesLoading = true }
            },
            onSuccess: { [weak self] _ in
                DispatchQueue.main.async { self?.isRepliesLoading = false }
            },
            onError: { [weak self] _ in
                DispatchQueue.main.async { self?.isRepliesLoading = false }
            }
        )

        // Replies list
        sharedViewModel.observeReplies { [weak self] kotlinReplies in
            DispatchQueue.main.async {
                self?.replies = kotlinReplies.map { QuestionReply(from: $0 as! ComposeApp.QuestionReply) }
            }
        }

        // Add reply state
        sharedViewModel.observeAddReplyState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isAddingReply = false }
            },
            onAdding: { [weak self] in
                DispatchQueue.main.async { self?.isAddingReply = true }
            },
            onSuccess: { [weak self] in
                DispatchQueue.main.async { self?.isAddingReply = false }
            },
            onError: { [weak self] _ in
                DispatchQueue.main.async { self?.isAddingReply = false }
            }
        )

        // Selected question
        sharedViewModel.observeSelectedQuestion { [weak self] kotlinQuestion in
            DispatchQueue.main.async {
                if let kq = kotlinQuestion {
                    self?.selectedQuestion = Question(from: kq)
                } else {
                    self?.selectedQuestion = nil
                }
            }
        }
    }

    // MARK: - Chat Actions

    func sendMessage(author: String, message: String) {
        sharedViewModel.sendMessage(author: author, message: message)
    }

    func refreshMessages() {
        sharedViewModel.refreshMessages()
    }

    // MARK: - Question Actions

    func addQuestion(author: String, title: String, description: String) {
        sharedViewModel.addQuestion(author: author, title: title, description: description)
    }

    func deleteQuestion(questionId: String) {
        sharedViewModel.deleteQuestion(questionId: questionId)
    }

    func refreshQuestions() {
        sharedViewModel.refreshQuestions()
    }

    // MARK: - Reply Actions

    func selectQuestion(question: Question) {
        sharedViewModel.selectQuestion(question: question.toKotlin())
    }

    func clearSelectedQuestion() {
        sharedViewModel.clearSelectedQuestion()
    }

    func addReply(questionId: String, author: String, content: String) {
        sharedViewModel.addReply(questionId: questionId, author: author, content: content)
    }

    func deleteReply(questionId: String, replyId: String) {
        sharedViewModel.deleteReply(questionId: questionId, replyId: replyId)
    }

    deinit {
        sharedViewModel.clear()
    }
}
