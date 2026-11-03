package org.example.rest;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transcricao")
public class TranscricaoController {

    private final ChatModel chatModel;

    public TranscricaoController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping
    public ResponseEntity<String> transcreverAudio(@RequestParam("audio") MultipartFile arquivo) {
        try {
            Media media = new Media(
                    MimeTypeUtils.parseMimeType(arquivo.getContentType()),
                    arquivo.getResource()
            );
            String instrucao = "Ouça este áudio e transcreva exatamente o que está sendo dito, " +
                    "traduzindo para o português se estiver em outro idioma. " +
                    "Retorne apenas o texto final.";

            UserMessage userMessage = UserMessage.builder().text(instrucao).media(media).build();
            String resposta = chatModel.call(new Prompt(userMessage))
                    .getResult()
                    .getOutput().getText();

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar o áudio: " + e.getMessage());
        }
    }
}