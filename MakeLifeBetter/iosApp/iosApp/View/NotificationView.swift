//
//  NotificationView.swift
//  iosApp
//

import SwiftUI
import UserNotifications

struct NotificationView: View {
    @State var viewModel = NotificationViewModel()

    var body: some View {
        VStack(spacing: 0) {
            // Header with scheduled count
            if viewModel.scheduledCount > 0 {
                ScheduledCountHeader(count: viewModel.scheduledCount)
            }

            // Permission request banner
            if !viewModel.hasPermission {
                PermissionBanner {
                    requestNotificationPermission()
                }
            }

            // Notifications list
            if viewModel.notifications.isEmpty {
                EmptyNotificationsView()
            } else {
                NotificationListView(
                    notifications: viewModel.notifications,
                    onDismiss: { id in
                        viewModel.dismissNotification(id: id)
                    },
                    onMarkAsRead: { id in
                        viewModel.markAsRead(id: id)
                    }
                )
            }
        }
        .onAppear {
            viewModel.refreshPermissionState()
        }
    }

    private func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
            DispatchQueue.main.async {
                viewModel.onPermissionResult(granted: granted)
            }
        }
    }
}

// MARK: - Scheduled Count Header

private struct ScheduledCountHeader: View {
    let count: Int

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text("Notificacoes agendadas")
                    .font(.headline)
                    .fontWeight(.bold)

                Text("\(count) eventos com lembrete")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            Spacer()
        }
        .padding(16)
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal, 16)
        .padding(.top, 16)
    }
}

// MARK: - Permission Banner

private struct PermissionBanner: View {
    let onRequestPermission: () -> Void

    var body: some View {
        VStack(spacing: 8) {
            Text("Permissao necessaria")
                .font(.headline)
                .fontWeight(.bold)
                .foregroundColor(.red)

            Text("Ative as notificacoes para receber lembretes de eventos")
                .font(.subheadline)
                .foregroundColor(.red.opacity(0.8))
                .multilineTextAlignment(.center)

            Button(action: onRequestPermission) {
                Text("Permitir notificacoes")
                    .fontWeight(.semibold)
                    .foregroundColor(.white)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(Color.red)
                    .cornerRadius(8)
            }
            .padding(.top, 4)
        }
        .frame(maxWidth: .infinity)
        .padding(16)
        .background(Color.red.opacity(0.1))
        .cornerRadius(12)
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }
}

// MARK: - Empty Notifications

private struct EmptyNotificationsView: View {
    var body: some View {
        VStack(spacing: 8) {
            Spacer()
            Text("Nenhuma notificacao")
                .font(.title3)
                .fontWeight(.medium)
                .foregroundColor(.secondary)

            Text("Os lembretes de eventos aparecerao aqui")
                .font(.subheadline)
                .foregroundColor(.secondary.opacity(0.7))
                .multilineTextAlignment(.center)
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(16)
    }
}

// MARK: - Notification List

private struct NotificationListView: View {
    let notifications: [AppNotification]
    let onDismiss: (String) -> Void
    let onMarkAsRead: (String) -> Void

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                ForEach(notifications) { notification in
                    NotificationCardView(
                        notification: notification,
                        onDismiss: { onDismiss(notification.id) },
                        onMarkAsRead: { onMarkAsRead(notification.id) }
                    )
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 8)
            .padding(.bottom, 32)
        }
    }
}

// MARK: - Notification Card

private struct NotificationCardView: View {
    let notification: AppNotification
    let onDismiss: () -> Void
    let onMarkAsRead: () -> Void

    private var statusText: String {
        if notification.isFired {
            return "Disparada"
        } else {
            return "Agendada para \(formatTime(notification.scheduledTime))"
        }
    }

    private var backgroundColor: Color {
        notification.isRead ? Color(.systemGray5) : Color(.systemBackground)
    }

    var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(alignment: .leading, spacing: 6) {
                // Title row with "Novo" badge
                HStack {
                    Text(notification.title)
                        .font(.system(size: 16, weight: .bold))
                        .lineLimit(2)

                    Spacer()

                    if !notification.isRead {
                        Text("Novo")
                            .font(.system(size: 10))
                            .foregroundColor(.white)
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color.blue)
                            .cornerRadius(4)
                    }
                }
                .padding(.trailing, 24)

                // Message
                Text(notification.message)
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
                    .lineSpacing(4)

                // Status and time row
                HStack {
                    Text(statusText)
                        .font(.system(size: 12, weight: notification.isFired ? .medium : .regular))
                        .foregroundColor(notification.isFired ? .blue : .secondary.opacity(0.6))

                    Spacer()

                    Text(formatRelativeTime(notification.createdAt))
                        .font(.system(size: 12))
                        .foregroundColor(.secondary.opacity(0.6))
                }
                .padding(.top, 2)
            }
            .padding(16)

            // Dismiss button
            Button(action: onDismiss) {
                Text("\u{2715}")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(.secondary)
                    .frame(width: 24, height: 24)
            }
            .padding(.top, 12)
            .padding(.trailing, 12)
            .accessibilityLabel("Remover notificacao")
        }
        .background(backgroundColor)
        .cornerRadius(12)
        .shadow(color: .black.opacity(notification.isRead ? 0.05 : 0.1), radius: notification.isRead ? 1 : 4, y: notification.isRead ? 1 : 2)
        .onTapGesture {
            if !notification.isRead {
                onMarkAsRead()
            }
        }
    }

    // MARK: - Time Formatting

    private func formatTime(_ epochMillis: Int64) -> String {
        let date = Date(timeIntervalSince1970: Double(epochMillis) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: date)
    }

    private func formatRelativeTime(_ epochMillis: Int64) -> String {
        let now = Date().timeIntervalSince1970 * 1000
        let diffMinutes = Int64(now - Double(epochMillis)) / 60000

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
}

#Preview {
    NotificationView()
}
