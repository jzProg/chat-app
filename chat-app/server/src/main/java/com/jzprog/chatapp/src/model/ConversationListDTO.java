package com.jzprog.chatapp.src.model;

import java.util.List;

public class ConversationListDTO {
    int total;
    List<ConversationDTO> conversationDTO;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ConversationDTO> getConversationDTO() {
        return conversationDTO;
    }

    public void setConversationDTO(List<ConversationDTO> conversationDTO) {
        this.conversationDTO = conversationDTO;
    }
}
