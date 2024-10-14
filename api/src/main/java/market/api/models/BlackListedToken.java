package market.api.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name="jwt_blacklist")
@Data
public class BlackListedToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String token;
	@Column(name = "expire_date")
	LocalDateTime expireDate;
}