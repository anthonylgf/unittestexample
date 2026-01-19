package com.example.unittestexample.subscriber.representation;

import com.example.unittestexample.enums.Genero;
import java.time.LocalDate;

public record AlunoRepresentation(String nomeCompleto, Genero genero, LocalDate dataNascimento) {}
