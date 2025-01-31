package kh.edu.social_members.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class Members {
    @Id
    @GeneratedValue

    private int id;
    private String nickname;
    private String username;
    private String password;
    private String email;

}
