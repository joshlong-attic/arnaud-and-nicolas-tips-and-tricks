package bootiful.moduliths.orders;

import org.springframework.modulith.events.Externalized;

@Externalized
public record OrderCreatedEvent(int productId, int quantity) {
}
