package net.rewerk.webstore.dto.mq.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO to send messages to write events SCS Queue
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventsWriteDto {
    /**
     * User identifier, bound for event
     */
    private UUID user_id;
    /**
     * Text of event
     */
    private String text;
}
