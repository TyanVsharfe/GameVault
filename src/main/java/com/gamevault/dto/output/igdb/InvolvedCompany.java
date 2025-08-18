package com.gamevault.dto.output.igdb;

public record InvolvedCompany(
        int id,
        boolean developer,
        boolean publisher,
        Company company
) {}
