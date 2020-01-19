package org.behnaz.rcsp;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

@Builder
@EqualsAndHashCode
@ToString
@Value
public class StateVariableValue {
	@Getter
	private String stateName;
	@Getter
	private Boolean value;
}