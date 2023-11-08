package bootiful.moduliths.orders;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
@Transactional
class Orders {

    private final OrderRepository orderRepository;

    private final ApplicationEventPublisher publisher;

    Orders(OrderRepository orderRepository, ApplicationEventPublisher publisher) {
        this.orderRepository = orderRepository;
        this.publisher = publisher;
    }

    void save(Order order) {
        this.orderRepository.save(order);
        this.publisher.publishEvent(new OrderCreatedEvent(order.productId(), order.quantity()));
    }
}

@Controller
@ResponseBody
class OrdersController {

    private final Orders orders;

    OrdersController(Orders orders) {
        this.orders = orders;
    }

    @PostMapping("/demo")
    void demo() throws Exception {
        this.orders.save(new Order(null, 3, 3));
    }
}

@Table("orders")
record Order(@Id Integer id, int productId, int quantity) {
}

interface OrderRepository extends ListCrudRepository<Order, Integer> {
}
