package market.api.factories;

import com.github.javafaker.Faker;

public abstract class Factory {
	protected final Faker faker;

	public Factory() {
		this.faker = new Faker();
	}
}
