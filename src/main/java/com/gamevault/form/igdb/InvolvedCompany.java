package com.gamevault.form.igdb;

public record InvolvedCompany(
        int id,
        boolean developer,
        boolean publisher,
        Company company
) {
}
