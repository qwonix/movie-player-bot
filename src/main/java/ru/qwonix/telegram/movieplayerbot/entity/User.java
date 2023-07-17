package ru.qwonix.telegram.movieplayerbot.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.pro.packaged.B;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.state.StateType;

import javax.persistence.*;
import java.io.IOException;

@NoArgsConstructor
@AllArgsConstructor
@Builder

@Data
@Entity
@Table(name = "`user`")
public class User {

    @Id
    private Long telegramId;
    private String firstName;

    private String lastName;
    private String username;
    private String languageCode;

    private boolean isAdmin;

    @Enumerated(EnumType.STRING)
    private StateType state;

    @Convert(converter = MessageIdsJsonConverter.class)
    private MessageIds messageIds;

    @Converter
    @Component
    private static class MessageIdsJsonConverter implements AttributeConverter<MessageIds, String> {
        private final ObjectMapper objectMapper;

        private MessageIdsJsonConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public String convertToDatabaseColumn(MessageIds messageIds) {
            try {
                return objectMapper.writeValueAsString(messageIds);
            } catch (JsonProcessingException ex) {
                return null;
                // or throw an error
            }
        }

        @Override
        public MessageIds convertToEntityAttribute(String jsonData) {
            try {
                return objectMapper.readValue(jsonData, MessageIds.class);
            } catch (IOException ex) {
                // logger.error("Unexpected IOEx decoding json from database: " + dbData);
                return null;
            }
        }

    }
}
