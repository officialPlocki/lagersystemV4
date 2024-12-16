package de.kabuecher.storage.v4.client.flows;

import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.SummarizingBody;
import de.kabuecher.storage.v4.client.panels.contentBodys.desktop.impl.ComponentType;
import de.kabuecher.storage.v4.sevdesk.SevDesk;
import de.kabuecher.storage.v4.sevdesk.impl.offer.Offer;
import de.kabuecher.storage.v4.sevdesk.impl.offer.OfferPos;

import java.awt.event.ActionEvent;
import java.util.List;

public class OrderViewFlow {

    public OrderViewFlow() {
        SevDesk sevDesk = new SevDesk();
        List<Offer> offers = sevDesk.getOpenOffers();
        SummarizingBody summarizingBody = new SummarizingBody(offers);
        for (String component : summarizingBody.getTypeComponents().keySet()) {
            summarizingBody.getTypeComponents().get(component).addAction("submit_button", new ComponentType.ActionEventRunnable() {
                @Override
                public void handleActionEvent(ActionEvent event) {
                    submitOrder(offers.stream().filter(offer -> offer.getId().equals(component)).findFirst().get());
                }

                @Override
                public void run() {

                }
            });
        }
    }

    private void submitOrder(Offer offer) {
        SevDesk sevDesk = new SevDesk();

        List<OfferPos> positions = sevDesk.getOrderPositions(offer.getId());

    }

}
