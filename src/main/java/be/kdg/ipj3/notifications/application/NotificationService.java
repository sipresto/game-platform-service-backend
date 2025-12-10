package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.api.dto.NotificationDto;
import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UsersApi usersApi;

    public NotificationService(NotificationRepository notificationRepository, UsersApi usersApi) {
        this.notificationRepository = notificationRepository;
        this.usersApi = usersApi;
    }

    public List<Notification> findAllNotifications(String keycloakId) {
        var user = usersApi.findByKeycloakId(keycloakId);

        return notificationRepository.findByUserId(user.getId());
    }
}
