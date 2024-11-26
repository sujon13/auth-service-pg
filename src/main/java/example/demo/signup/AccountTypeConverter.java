package example.demo.signup;

import example.demo.signup.enums.AccountType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class AccountTypeConverter implements AttributeConverter<AccountType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccountType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public AccountType convertToEntityAttribute(Integer dbData) {
        return AccountType.getByValue(dbData);
    }
}