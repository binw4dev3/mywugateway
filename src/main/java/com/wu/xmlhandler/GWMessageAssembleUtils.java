package com.wu.xmlhandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wu.gw.model.ExcelDataEntry;
import com.wu.gw.model.WUTransaction;
import com.wu.gw.model.ais.GeneralName;
import com.wu.gw.model.ais.IsoCode;
import com.wu.gw.model.ais.NameType;
import com.wu.gw.util.UtilFunctions;

public class GWMessageAssembleUtils {

	private GWMessageAssembleUtils() {
	}

	public static String assembleAISSMVRequestMsg(XMLAssemblerHandler xmlHandler, String msg, ExcelDataEntry sData) {

		List<String> senderNameValues = null;
		GeneralName sName = sData.getSenderName();
		if (sName.getNameType().equals(NameType.D)) {

			senderNameValues = Arrays.asList(NameType.D.toString(), sName.getFirstName(), sName.getMiddleName(),
					sName.getLastName());

		} else if (sName.getNameType().equals(NameType.M)) {

			senderNameValues = Arrays.asList(NameType.M.toString(), sName.getGivenName(), sName.getPaternalName(),
					sName.getMaternalName());

		} else if (sName.getNameType().equals(NameType.C)) {

			senderNameValues = Arrays.asList(NameType.C.toString(), sName.getBusinessName());
		}

		List<String> receiverNameValues = null;
		GeneralName rName = sData.getReceiverName();
		if (rName.getNameType().equals(NameType.D)) {

			receiverNameValues = Arrays.asList(NameType.D.toString(), rName.getFirstName(), rName.getMiddleName(),
					rName.getLastName());

		} else if (rName.getNameType().equals(NameType.M)) {

			receiverNameValues = Arrays.asList(NameType.M.toString(), rName.getGivenName(), rName.getPaternalName(),
					rName.getMaternalName());

		} else if (rName.getNameType().equals(NameType.C)) {

			receiverNameValues = Arrays.asList(NameType.C.toString(), rName.getBusinessName());
		}

		List<String> financialValues = Arrays.asList(sData.getTransactionType(),
				UtilFunctions.convertToWUAmount(sData.getAmount()));

		IsoCode rCountryCurrency = sData.getReceiveCountryCurrency().getIsoCode();
		List<String> destValues = Arrays.asList(rCountryCurrency.getCountryCode(), rCountryCurrency.getCurrencyCode());

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		String sNameKey = elements.get("smv.sender.name");
		AbstractXMLAssembler sNameAssembler = assemblers.get("smv.sender.name.assembler");

		msg = sNameAssembler.assemble(msg, sNameKey, senderNameValues);

		String rNameKey = elements.get("smv.receiver.name");
		AbstractXMLAssembler rNameAssembler = assemblers.get("smv.receiver.name.assembler");

		msg = rNameAssembler.assemble(msg, rNameKey, receiverNameValues);

		String finKey = elements.get("smv.financials.amount");
		AbstractXMLAssembler finAssembler = assemblers.get("smv.financials.amount.assembler");

		msg = finAssembler.assemble(msg, finKey, financialValues);

		String txnTypeKey = elements.get("smv.payment_details.transaction_type");
		AbstractXMLAssembler txnTypeAssembler = assemblers.get("smv.payment_details.transaction_type.assembler");

		msg = txnTypeAssembler.assemble(msg, txnTypeKey, financialValues);

		String destKey = elements.get("smv.payment_details.destination_country_currency");
		AbstractXMLAssembler destAssembler = assemblers.get("smv.payment_details.destination_country_currency.assembler");

		msg = destAssembler.assemble(msg, destKey, destValues);

		List<String> sAddressValues = Arrays.asList(sData.getSenderAddress());
		String sAddressKey = elements.get("smv.sender.address");
		AbstractXMLAssembler sAddressAssembler = assemblers.get("smv.sender.address.assembler");
		msg = sAddressAssembler.assemble(msg, sAddressKey, sAddressValues);

		return msg;
	}

	public static String assembleAISSMSRequestMsg(XMLAssemblerHandler xmlHandler, String msg, String smvRequest,
			String smvReply) {

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		List<String> smvReplyList = Arrays.asList(

				smvReply);

		List<String> smvRequestList = Arrays.asList(

				smvRequest);

		for (String key : elements.keySet()) {

			if (key.startsWith("sms.")) {

				String element = elements.get(key);
				AbstractXMLAssembler assembler = assemblers.get(key + ".assembler");

				if (key.endsWith(".sender") || key.endsWith(".receiver")) {

					msg = assembler.assemble(msg, element, smvRequestList);

				} else {

					msg = assembler.assemble(msg, element, smvReplyList);

				}
			}
		}

		return msg;
	}

	public static String assembleAISSearchRequestMsg(XMLAssemblerHandler xmlHandler, String msg, ExcelDataEntry sData) {

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		String mtcnKey = elements.get("search.mtcn");
		AbstractXMLAssembler mtcnAssembler = assemblers.get("search.mtcn.assembler");

		msg = mtcnAssembler.assemble(msg, mtcnKey, Arrays.asList(sData.getMTCN()));

		return msg;
	}

	public static String assembleAISRefundRequestMsg(XMLAssemblerHandler xmlHandler, String msg, String searchReply) {

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		List<String> searchReplyList = Arrays.asList(searchReply);

		for (String key : elements.keySet()) {

			if (key.startsWith("refund.")) {

				String element = elements.get(key);
				AbstractXMLAssembler assembler = assemblers.get(key + ".assembler");

				msg = assembler.assemble(msg, element, searchReplyList);
			}
		}

		return msg;
	}
	

	public static String assembleAISFIRequestMsg(XMLAssemblerHandler xmlHandler, String msg, ExcelDataEntry sData) {

		List<String> financialValues = Arrays.asList(sData.getTransactionType(), UtilFunctions.convertToWUAmount(sData.getAmount()));

		IsoCode rCountryCurrency = sData.getReceiveCountryCurrency().getIsoCode();
		List<String> destValues = Arrays.asList(rCountryCurrency.getCountryCode(), rCountryCurrency.getCurrencyCode());

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		String finKey = elements.get("fi.financials.amount");
		AbstractXMLAssembler finAssembler = assemblers.get("fi.financials.amount.assembler");

		msg = finAssembler.assemble(msg, finKey, financialValues);

		String txnTypeKey = elements.get("fi.payment_details.transaction_type");
		AbstractXMLAssembler txnTypeAssembler = assemblers.get("fi.payment_details.transaction_type.assembler");

		msg = txnTypeAssembler.assemble(msg, txnTypeKey, financialValues);

		String destKey = elements.get("fi.payment_details.destination_country_currency");
		AbstractXMLAssembler destAssembler = assemblers.get("fi.payment_details.destination_country_currency.assembler");

		msg = destAssembler.assemble(msg, destKey, destValues);
		
		String deliveryCodeKey = elements.get("fi.delivery_services.code");
		AbstractXMLAssembler deliveryCodeAssembler = assemblers.get("fi.delivery_services.code.assembler");

		msg = deliveryCodeAssembler.assemble(msg, deliveryCodeKey, Arrays.asList(sData.getServiceType()));

		return msg;
	}

	public static String assembleAISFIRequestMsgForPricing(XMLAssemblerHandler xmlHandler, String msg, ExcelDataEntry sData) {

		List<String> financialValues = Arrays.asList(sData.getTransactionType(), UtilFunctions.convertToWUAmount(sData.getAmount()));

		IsoCode rCountryCurrency = sData.getReceiveCountryCurrency().getIsoCode();
		List<String> destValues = Arrays.asList(rCountryCurrency.getCountryCode(), rCountryCurrency.getCurrencyCode());

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		String finKey = elements.get("fi.financials.amount");
		AbstractXMLAssembler finAssembler = assemblers.get("fi.financials.amount.assembler");

		msg = finAssembler.assemble(msg, finKey, financialValues);

		String txnTypeKey = elements.get("fi.payment_details.transaction_type");
		AbstractXMLAssembler txnTypeAssembler = assemblers.get("fi.payment_details.transaction_type.assembler");

		msg = txnTypeAssembler.assemble(msg, txnTypeKey, financialValues);

		String destKey = elements.get("fi.payment_details.destination_country_currency");
		AbstractXMLAssembler destAssembler = assemblers.get("fi.payment_details.destination_country_currency.assembler");

		msg = destAssembler.assemble(msg, destKey, destValues);

		String deliveryCodeKey = elements.get("fi.delivery_services.code");
		AbstractXMLAssembler deliveryCodeAssembler = assemblers.get("fi.delivery_services.code.assembler");

		msg = deliveryCodeAssembler.assemble(msg, deliveryCodeKey, Arrays.asList(sData.getServiceType()));

		String counterId = elements.get("fi.foreign_remote_system.counter_id");
		AbstractXMLAssembler counterIdAssembler = assemblers.get("fi.foreign_remote_system.counter_id.assembler");

		msg = counterIdAssembler.assemble(msg, counterId, Arrays.asList(sData.getCounterId()));

		return msg;
	}

	public static String assembleBISFIRequestMsg(XMLAssemblerHandler xmlHandler, String msg, ExcelDataEntry sData) {

		List<String> txnTypeValues = Arrays.asList(sData.getTransactionType());

		IsoCode sCountryCurrency = sData.getSendCountryCurrency().getIsoCode();
		List<String> sFinancialValues = Arrays.asList(UtilFunctions.convertToWUAmount(sData.getAmount()),
				sCountryCurrency.getCurrencyCode(), sCountryCurrency.getCountryCode());

		IsoCode rCountryCurrency = sData.getReceiveCountryCurrency().getIsoCode();
		List<String> rFinancialValues = Arrays.asList(UtilFunctions.convertToWUAmount(sData.getAmount()),
				rCountryCurrency.getCurrencyCode(), rCountryCurrency.getCountryCode());

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		String txnTypeKey = elements.get("fi.transaction_type");
		AbstractXMLAssembler txnTypeAssembler = assemblers.get("fi.transaction_type.assembler");

		msg = txnTypeAssembler.assemble(msg, txnTypeKey, txnTypeValues);

		String principalKey = elements.get("fi.payment_details.principal_amount");
		AbstractXMLAssembler principalAssembler = assemblers.get("fi.payment_details.principal_amount.assembler");

		msg = principalAssembler.assemble(msg, principalKey, sFinancialValues);

		String destKey = elements.get("fi.payment_details.destination");
		AbstractXMLAssembler destAssembler = assemblers.get("fi.payment_details.destination.assembler");

		msg = destAssembler.assemble(msg, destKey, rFinancialValues);

		return msg;
	}

	public static String assembleBISFIRequestMsgForPricing(XMLAssemblerHandler xmlHandler, String msg, ExcelDataEntry sData) {

		List<String> txnTypeValues = Arrays.asList(sData.getTransactionType());

		IsoCode sCountryCurrency = sData.getSendCountryCurrency().getIsoCode();
		List<String> sFinancialValues = Arrays.asList(UtilFunctions.convertToWUAmount(sData.getAmount()),
				sCountryCurrency.getCurrencyCode(), sCountryCurrency.getCountryCode());

		IsoCode rCountryCurrency = sData.getReceiveCountryCurrency().getIsoCode();
		List<String> rFinancialValues = Arrays.asList(UtilFunctions.convertToWUAmount(sData.getAmount()),
				rCountryCurrency.getCurrencyCode(), rCountryCurrency.getCountryCode());

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		String txnTypeKey = elements.get("fi.transaction_type");
		AbstractXMLAssembler txnTypeAssembler = assemblers.get("fi.transaction_type.assembler");

		msg = txnTypeAssembler.assemble(msg, txnTypeKey, txnTypeValues);

		String principalKey = elements.get("fi.payment_details.principal_amount");
		AbstractXMLAssembler principalAssembler = assemblers.get("fi.payment_details.principal_amount.assembler");

		msg = principalAssembler.assemble(msg, principalKey, sFinancialValues);

		String destKey = elements.get("fi.payment_details.destination");
		AbstractXMLAssembler destAssembler = assemblers.get("fi.payment_details.destination.assembler");

		msg = destAssembler.assemble(msg, destKey, rFinancialValues);

		String counterId = elements.get("fi.connector");
		AbstractXMLAssembler counterIdAssembler = assemblers.get("fi.connector.assembler");

		msg = counterIdAssembler.assemble(msg, counterId, Arrays.asList(sData.getCounterId()));

		return msg;
	}

	public static String assembleBISSMVRequestMsg(XMLAssemblerHandler xmlHandler, String msg, ExcelDataEntry sData,
			String fiReply) {

		List<String> senderNameValues = null;
		GeneralName sName = sData.getSenderName();
		if (sName.getNameType().equals(NameType.D)) {

			senderNameValues = Arrays.asList(NameType.D.toString(), sName.getFirstName(), sName.getMiddleName(),
					sName.getLastName());

		} else if (sName.getNameType().equals(NameType.M)) {

			senderNameValues = Arrays.asList(NameType.M.toString(), sName.getGivenName(), sName.getPaternalName(),
					sName.getMaternalName());

		} else if (sName.getNameType().equals(NameType.C)) {

			senderNameValues = Arrays.asList(NameType.C.toString(), sName.getBusinessName());
		}

		List<String> receiverNameValues = null;
		GeneralName rName = sData.getReceiverName();
		if (rName.getNameType().equals(NameType.D)) {

			receiverNameValues = Arrays.asList(NameType.D.toString(), rName.getFirstName(), rName.getMiddleName(),
					rName.getLastName());

		} else if (rName.getNameType().equals(NameType.M)) {

			receiverNameValues = Arrays.asList(NameType.M.toString(), rName.getGivenName(), rName.getPaternalName(),
					rName.getMaternalName());

		} else if (rName.getNameType().equals(NameType.C)) {

			receiverNameValues = Arrays.asList(NameType.C.toString(), rName.getBusinessName());
		}

		List<String> fiReplyList = Arrays.asList(

				fiReply);

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		String sNameKey = elements.get("smv.sender.name");
		AbstractXMLAssembler sNameAssembler = assemblers.get("smv.sender.name.assembler");

		msg = sNameAssembler.assemble(msg, sNameKey, senderNameValues);

		String rNameKey = elements.get("smv.receiver.name");
		AbstractXMLAssembler rNameAssembler = assemblers.get("smv.receiver.name.assembler");

		msg = rNameAssembler.assemble(msg, rNameKey, receiverNameValues);

		String txnTypeKey = elements.get("smv.transaction_type");
		AbstractXMLAssembler txnTypeAssembler = assemblers.get("smv.transaction_type.assembler");

		msg = txnTypeAssembler.assemble(msg, txnTypeKey, fiReplyList);

		String paymentKey = elements.get("smv.payment_details");
		AbstractXMLAssembler paymentAssembler = assemblers.get("smv.payment_details.assembler");

		msg = paymentAssembler.assemble(msg, paymentKey, fiReplyList);

		List<String> sAddressValues = Arrays.asList(sData.getSenderAddress());
		String sAddressKey = elements.get("smv.sender.address");
		AbstractXMLAssembler sAddressAssembler = assemblers.get("smv.sender.address.assembler");
		msg = sAddressAssembler.assemble(msg, sAddressKey, sAddressValues);

		return msg;
	}

	public static String assembleBISSMSRequestMsg(XMLAssemblerHandler xmlHandler, String msg, String smvRequest,
			String smvReply) {

		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();

		List<String> smvReplyList = Arrays.asList(

				smvReply);

		List<String> smvRequestList = Arrays.asList(

				smvRequest);

		for (String key : elements.keySet()) {

			if (key.startsWith("sms.")) {

				String element = elements.get(key);
				AbstractXMLAssembler assembler = assemblers.get(key + ".assembler");

				if (key.endsWith(".sender") || key.endsWith(".receiver")) {

					msg = assembler.assemble(msg, element, smvRequestList);

				} else {

					msg = assembler.assemble(msg, element, smvReplyList);

				}
			}
		}

		return msg;
	}

	public static String assembleDASRequestMsg(XMLAssemblerHandler xmlHandler, String dasRequest,
			WUTransaction txnData) {
		
		Map<String, String> dasFilterMap = txnData.getDasFilters();
		HashMap<String, String> elements = xmlHandler.getElements();
		HashMap<String, AbstractXMLAssembler> assemblers = xmlHandler.getAssemblers();
		
		for(Entry<String, String> filterEntry : dasFilterMap.entrySet()) {
			String filter = filterEntry.getKey();
			String filterValue = filterEntry.getValue();
			
			String filterKey = elements.get(filter);
			
			int filterIndex = dasRequest.indexOf("<" + filter + ">");
			if(filterIndex == -1) {
				int sIndex = dasRequest.indexOf("<filters>");
				int eIndex = dasRequest.indexOf("</filters>");
				if(sIndex == -1 || eIndex == -1 || sIndex >= eIndex) return dasRequest;
				
				dasRequest = dasRequest.substring(0, eIndex) + "<" + filter + ">" + "</" + filter + ">" + dasRequest.substring(eIndex);
			}
			
			AbstractXMLAssembler filterAssembler = assemblers.get(filter + ".assembler");
			dasRequest = filterAssembler.assemble(dasRequest, filterKey, Arrays.asList(filterValue));
		}

		return dasRequest;
	}
}
