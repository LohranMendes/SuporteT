package com.gruposuporte.projetosuporte.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(Model model){
      model.addAttribute("error_message","Imagem excedeu o tamanho permitido.");
      return "redirect:/realizar-call";
    }
}
