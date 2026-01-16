package com.test.demoproductkumamesh.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultiStatusResponse<T> {
    private List<T> successes;
    private Integer successCount;
    private List<T> failures;
    private Integer failureCount;

    public MultiStatusResponse(List<T> successes, List<T> failures) {
        this.successes = successes;
        this.failures = failures;
        this.successCount = successes.size();
        this.failureCount = failures.size();
    }

    private MultiStatusResponse(MultiStatusResponseBuilder<T> multiStatusResponse) {
        this.successes = multiStatusResponse.successes;
        this.failures = multiStatusResponse.failures;
        this.successCount = multiStatusResponse.successCount;
        this.failureCount = multiStatusResponse.failureCount;
    }

    public void setSuccesses(List<T> successes) {
        this.successes = successes;
        this.successCount = successes.size();
    }

    public void setFailures(List<T> failures) {
        this.failures = failures;
        this.failureCount = failures.size();
    }

    public static <T> MultiStatusResponseBuilder<T> builder() {
        return new MultiStatusResponseBuilder<>();
    }

    public static class MultiStatusResponseBuilder<T> {
        private List<T> successes;
        private Integer successCount;
        private List<T> failures;
        private Integer failureCount;

        public MultiStatusResponseBuilder<T> successes(List<T> successes) {
            this.successes = successes;
            this.successCount = successes.size();
            return this;
        }

        public MultiStatusResponseBuilder<T> failures(List<T> failures) {
            this.failures = failures;
            this.failureCount = failures.size();
            return this;
        }

        public MultiStatusResponse<T> build() {
            return new MultiStatusResponse<>(this);
        }
    }
}
