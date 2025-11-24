package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserJpaEntity {
    @Id
    private String id;

    private String username;

    protected UserJpaEntity() {
    }

    public UserJpaEntity(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User toDomain(){
        return new User(new UserId(id), username);
    }

    public static UserJpaEntity fromDomain(User user){
        return new UserJpaEntity(user.getId().value(), user.getUsername());
    }
}