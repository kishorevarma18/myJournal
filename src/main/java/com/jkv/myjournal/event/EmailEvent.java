package com.jkv.myjournal.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//used Builder here because it is easier to assign the values to the class rather than using a constructor.
//readable method-chaining, no parameter alignment errors are some of the feature of builder.
@Builder
@NoArgsConstructor
@AllArgsConstructor
//while Kafka won't look at Serializable because it converts things into JSON strings. 
//it is maintained as an insurance policy for architectural scaling (like introducing Redis caching layer down the road).
public class EmailEvent implements Serializable {
    // Explicit tracking version used by native Java binary serialization engines to verify 
    // that both the sender and receiver classes have identical fields during runtime execution.
    private static final long serialVersionUID=1L;

    private String to;
    private String subject;
    private String body;
}
