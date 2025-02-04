package org.biovars.bioinformaticsportal.hpc.resourceaccount;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Table("RESOURCE_ACCOUNTS")
public class ResourceAccount {
    @Id
    private Long id;
    private String name;
    private BigDecimal balance;
    private boolean active;

    private ResourceAccount() {
        this.id = null;
        this.name = null;
        this.balance = null;
    }
    public ResourceAccount(Long id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }
    public String getName() {
        return name;
    }

    public void setName(String accountName) {
        this.name = accountName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourceAccount) {
            return this.name.equals(((ResourceAccount) obj).name);
        }
        return false;
    }

    public static ResourceAccount parseHpcAccountString(String accRecord) {
        final int attributeIdx = 2;
        final int balanceIdx = 3;

        ResourceAccount result = new ResourceAccount();
        accRecord = accRecord.strip();
        var fields = accRecord.split("\\|");
        Map<String, String> attributes = Arrays.stream(fields[attributeIdx].split(","))
                .map(line -> line.split("="))
                .collect(Collectors.toMap(
                        kv -> kv[0],
                        kv -> kv[1] ));

        String accName = attributes.get("Account");
        result.setName(accName);
        var balance = BigDecimal.valueOf(Double.valueOf(fields[balanceIdx]));
        result.setBalance(balance);

        return result;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
