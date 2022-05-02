package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LoanerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Loaner.class);
        Loaner loaner1 = new Loaner();
        loaner1.setId("id1");
        Loaner loaner2 = new Loaner();
        loaner2.setId(loaner1.getId());
        assertThat(loaner1).isEqualTo(loaner2);
        loaner2.setId("id2");
        assertThat(loaner1).isNotEqualTo(loaner2);
        loaner1.setId(null);
        assertThat(loaner1).isNotEqualTo(loaner2);
    }
}
