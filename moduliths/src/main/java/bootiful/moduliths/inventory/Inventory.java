package bootiful.moduliths.inventory;

import bootiful.moduliths.orders.OrderCreatedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class Inventory {

    @ApplicationModuleListener
    void handleOrderCreatedEvent(OrderCreatedEvent oce) throws Exception {
        System.out.println("about to handle new order! need to update inventory for " +
                oce);
        Thread.sleep(1000 * 60);
        System.out.println("got a new order! need to update inventory for " +
                oce);
    }
}
