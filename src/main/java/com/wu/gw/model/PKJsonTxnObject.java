package com.wu.gw.model;

import java.util.LinkedList;
import java.util.List;

public class PKJsonTxnObject {

    private Transaction transaction = new Transaction();;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return "PKJsonTxnObject{" +
                "transaction=" + transaction +
                '}';
    }

    public class Transaction {
        private String mgiTransactionId = "";
        private String receiveCountryCode = "PAK";
        private String sendCountryCode = "USA";

        private Amount receiveAmount = new Amount();

        private Sender sender = new Sender();
        private Receiver receiver = new Receiver();

        private List<AdditionalData> additionalData = new LinkedList<>();

        public Transaction() {
            additionalData.add(new AdditionalData("senderAddressLine1", "85,Madurai Meenakshi Maasi street"));
            additionalData.add(new AdditionalData("senderDateOfBirth", "2004-11-29"));
            additionalData.add(new AdditionalData("senderIdType", "01"));
            additionalData.add(new AdditionalData("senderIdNumber", "113458964"));
            additionalData.add(new AdditionalData("purposeOfTransaction", ""));
            additionalData.add(new AdditionalData("senderNationality", "PAK"));
            additionalData.add(new AdditionalData("agentCode", "062092014"));
            additionalData.add(new AdditionalData("username", "092014001"));
            additionalData.add(new AdditionalData("password", "cashexpress"));
            additionalData.add(new AdditionalData("senderPhoneNumberMasked", "######43515"));
            additionalData.add(new AdditionalData("senderSourceOfFunds", ""));
            additionalData.add(new AdditionalData("senderPlaceOfBirth", ""));
            additionalData.add(new AdditionalData("senderProfession", ""));
            additionalData.add(new AdditionalData("senderRelationshipToBeneficiary", ""));
            additionalData.add(new AdditionalData("senderEmployer", ""));
            additionalData.add(new AdditionalData("receiverNationality", ""));
            additionalData.add(new AdditionalData("transactionType", "CASH"));
            additionalData.add(new AdditionalData("senderIdExpDate", "2027-04-26"));
        }

        public String getMgiTransactionId() {
            return mgiTransactionId;
        }

        public void setMgiTransactionId(String mgiTransactionId) {
            this.mgiTransactionId = mgiTransactionId;
        }

        public String getReceiveCountryCode() {
            return receiveCountryCode;
        }

        public void setReceiveCountryCode(String receiveCountryCode) {
            this.receiveCountryCode = receiveCountryCode;
        }

        public String getSendCountryCode() {
            return sendCountryCode;
        }

        public void setSendCountryCode(String sendCountryCode) {
            this.sendCountryCode = sendCountryCode;
        }

        public Amount getReceiveAmount() {
            return receiveAmount;
        }

        public void setReceiveAmount(Amount receiveAmount) {
            this.receiveAmount = receiveAmount;
        }

        public Sender getSender() {
            return sender;
        }

        public void setSender(Sender sender) {
            this.sender = sender;
        }

        public Receiver getReceiver() {
            return receiver;
        }

        public void setReceiver(Receiver receiver) {
            this.receiver = receiver;
        }

        public List<AdditionalData> getAdditionalData() {
            return additionalData;
        }

        public void setAdditionalData(List<AdditionalData> additionalData) {
            this.additionalData = additionalData;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "mgiTransactionId='" + mgiTransactionId + '\'' +
                    ", receiveCountryCode='" + receiveCountryCode + '\'' +
                    ", sendCountryCode='" + sendCountryCode + '\'' +
                    ", amount=" + receiveAmount +
                    ", sender=" + sender +
                    ", receiver=" + receiver +
                    ", additionalData=" + additionalData +
                    '}';
        }
    }

    public class Amount {
        private String value = "";
        private String currencyCode = "PKR";

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        @Override
        public String toString() {
            return "Amount{" +
                    "value='" + value + '\'' +
                    ", currencyCode='" + currencyCode + '\'' +
                    '}';
        }
    }

    public class Sender {
        private Person person = new Person();

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        @Override
        public String toString() {
            return "Sender{" +
                    "person=" + person +
                    '}';
        }
    }

    public class Receiver {
        private Person person = new Person();

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        @Override
        public String toString() {
            return "Sender{" +
                    "person=" + person +
                    '}';
        }
    }

    public class Person {
        private String firstName = "";
        private String middleName = "";
        private String lastName = "";
        private String secondLastName = "";

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getSecondLastName() {
            return secondLastName;
        }

        public void setSecondLastName(String secondLastName) {
            this.secondLastName = secondLastName;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "firstName='" + firstName + '\'' +
                    ", middleName='" + middleName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", secondLastName='" + secondLastName + '\'' +
                    '}';
        }
    }

    public class AdditionalData {
        private String key = "";
        private String value = "";

        public AdditionalData(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "AdditionalData{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
