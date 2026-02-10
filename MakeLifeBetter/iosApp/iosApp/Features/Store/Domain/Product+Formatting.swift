import Foundation
import ComposeApp

extension Product {
    var formattedPrice: String {
        String(format: "R$ %.2f", preco)
    }
}
