package hello.itemservice.validation;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.*;

public class MessageCodesResolverTest {

    MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] messages = messageCodesResolver.resolveMessageCodes("required", "item");
        assertThat(messages).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] messages = messageCodesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        assertThat(messages).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
