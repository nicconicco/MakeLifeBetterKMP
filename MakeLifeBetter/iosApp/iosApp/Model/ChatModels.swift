import Foundation
import ComposeApp

struct ChatMessage: Identifiable {
    var id: String
    var author: String
    var message: String
    var timestamp: Int64

    init(id: String, author: String, message: String, timestamp: Int64) {
        self.id = id
        self.author = author
        self.message = message
        self.timestamp = timestamp
    }

    init(from kotlin: ComposeApp.ChatMessage) {
        self.id = kotlin.id
        self.author = kotlin.author
        self.message = kotlin.message
        self.timestamp = kotlin.timestamp
    }
}

struct Question: Identifiable, Hashable {
    var id: String
    var title: String
    var description: String
    var author: String
    var replies: Int32
    var timestamp: Int64

    init(id: String, title: String, description: String, author: String, replies: Int32, timestamp: Int64) {
        self.id = id
        self.title = title
        self.description = description
        self.author = author
        self.replies = replies
        self.timestamp = timestamp
    }

    init(from kotlin: ComposeApp.Question) {
        self.id = kotlin.id
        self.title = kotlin.title
        self.description = kotlin.description_
        self.author = kotlin.author
        self.replies = kotlin.replies
        self.timestamp = kotlin.timestamp
    }

    func toKotlin() -> ComposeApp.Question {
        return ComposeApp.Question(
            id: id,
            title: title,
            description: description,
            author: author,
            replies: replies,
            timestamp: timestamp
        )
    }
}

struct QuestionReply: Identifiable {
    var id: String
    var questionId: String
    var author: String
    var content: String
    var timestamp: Int64

    init(id: String, questionId: String, author: String, content: String, timestamp: Int64) {
        self.id = id
        self.questionId = questionId
        self.author = author
        self.content = content
        self.timestamp = timestamp
    }

    init(from kotlin: ComposeApp.QuestionReply) {
        self.id = kotlin.id
        self.questionId = kotlin.questionId
        self.author = kotlin.author
        self.content = kotlin.content
        self.timestamp = kotlin.timestamp
    }
}

// MARK: - Time Formatting Helpers

func formatTime(epochMillis: Int64) -> String {
    let date = Date(timeIntervalSince1970: Double(epochMillis) / 1000.0)
    let formatter = DateFormatter()
    formatter.dateFormat = "HH:mm"
    return formatter.string(from: date)
}

func formatRelativeTime(epochMillis: Int64) -> String {
    let now = Int64(Date().timeIntervalSince1970 * 1000)
    let diffMinutes = (now - epochMillis) / 60000

    if diffMinutes < 0 {
        return "Em \(-diffMinutes) minutos"
    } else if diffMinutes < 1 {
        return "Agora"
    } else if diffMinutes < 60 {
        return "\(diffMinutes) minutos atras"
    } else if diffMinutes < 1440 {
        return "\(diffMinutes / 60) horas atras"
    } else {
        return "\(diffMinutes / 1440) dias atras"
    }
}
