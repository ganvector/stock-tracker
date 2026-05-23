package com.ganvector.stocktracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxReportResponse {

    private int referenceYear;
    private List<TaxReportEntry> entries;
}
