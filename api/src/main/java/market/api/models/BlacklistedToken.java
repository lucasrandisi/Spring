package market.api.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name="jwt_blacklist")
@Data
public class BlacklistedToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String token;
	@Column(name = "expire_date")
	Date expireDate;

}