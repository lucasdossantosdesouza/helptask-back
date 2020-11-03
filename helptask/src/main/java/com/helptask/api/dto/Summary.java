package com.helptask.api.dto;

import java.io.Serializable;

public class Summary implements Serializable {

    private Integer amountNew;
    private Integer amountAssigned;
    private Integer amountResolved;
    private Integer amountAproved;
    private Integer amountDisaproved;
    private Integer amountClosed;

    public Integer getAmountNew() {
        return amountNew;
    }

    public void setAmountNew(Integer amountNew) {
        this.amountNew = amountNew;
    }

    public Integer getAmountAssigned() {
        return amountAssigned;
    }

    public void setAmountAssigned(Integer amountAssigned) {
        this.amountAssigned = amountAssigned;
    }

    public Integer getAmountResolved() {
        return amountResolved;
    }

    public void setAmountResolved(Integer amountResolved) {
        this.amountResolved = amountResolved;
    }

    public Integer getAmountAproved() {
        return amountAproved;
    }

    public void setAmountAproved(Integer amountAproved) {
        this.amountAproved = amountAproved;
    }

    public Integer getAmountDisaproved() {
        return amountDisaproved;
    }

    public void setAmountDisaproved(Integer amountDisaproved) {
        this.amountDisaproved = amountDisaproved;
    }

    public Integer getAmountClosed() {
        return amountClosed;
    }

    public void setAmountClosed(Integer amountClosed) {
        this.amountClosed = amountClosed;
    }
}
