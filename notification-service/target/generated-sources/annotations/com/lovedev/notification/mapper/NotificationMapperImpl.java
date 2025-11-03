package com.lovedev.notification.mapper;

import com.lovedev.notification.model.dto.response.NotificationResponse;
import com.lovedev.notification.model.entity.Notification;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-03T15:43:39+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toResponse(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationResponse.NotificationResponseBuilder notificationResponse = NotificationResponse.builder();

        notificationResponse.body( notification.getBody() );
        notificationResponse.id( notification.getId() );
        notificationResponse.title( notification.getTitle() );
        notificationResponse.type( notification.getType() );
        notificationResponse.status( notification.getStatus() );
        notificationResponse.actionUrl( notification.getActionUrl() );
        notificationResponse.sentAt( notification.getSentAt() );
        notificationResponse.readAt( notification.getReadAt() );
        notificationResponse.createdAt( notification.getCreatedAt() );

        return notificationResponse.build();
    }

    @Override
    public List<NotificationResponse> toResponseList(List<Notification> notifications) {
        if ( notifications == null ) {
            return null;
        }

        List<NotificationResponse> list = new ArrayList<NotificationResponse>( notifications.size() );
        for ( Notification notification : notifications ) {
            list.add( toResponse( notification ) );
        }

        return list;
    }
}
