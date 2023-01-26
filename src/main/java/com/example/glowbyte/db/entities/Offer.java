package com.example.glowbyte.db.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Offer
{
    Long offerId;
    Boolean exposable;
    String clientFIO;
}
