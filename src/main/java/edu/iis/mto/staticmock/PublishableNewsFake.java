package edu.iis.mto.staticmock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad Gos on 03.04.2017.
 */
public class PublishableNewsFake extends PublishableNews {
    private final List<String> publicContent = new ArrayList<>();
    private final List<String> subscribentContent = new ArrayList<>();

    public List<String> getPublicContent() {
        return publicContent;
    }

    public List<String> getSubscribentContent() {
        return subscribentContent;
    }

    @Override
    public void addPublicInfo(String content) {
        super.addPublicInfo(content);
        this.publicContent.add(content);
    }

    @Override
    public void addForSubscription(String content, SubsciptionType subscriptionType) {
        super.addForSubscription(content, subscriptionType);
        subscribentContent.add(content);
    }
}
