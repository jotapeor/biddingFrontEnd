package com.bidding.system.frontend.controller;

import com.bidding.system.frontend.model.EditalDTO;
import com.bidding.system.frontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EditalController {

    @Autowired
    private ApiService restService;

    @GetMapping("/editais")
    public String editais(
            Model model,
            HttpSession session,
            @RequestParam(value = "urgente", required = false, defaultValue = "false") boolean urgente
    ) {

        String token = (String) session.getAttribute("token");

        List<EditalDTO> editais = restService.listarEditais(token, urgente);

        model.addAttribute("editais", editais);
        model.addAttribute("urgente", urgente);

        return "editais";
    }
}