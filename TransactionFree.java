//根据交易的txid调用钱包api获取相应信息
public Object getrawTransactionByTxIdFromWallet(String txId) throws Exception {
		BtcTransactionInfo 	tranc = new BtcTransactionInfo();
		try {
			//调用原始交易接口
			Map transaction = (Map) btService.getTrawtransaction(txId, 1);
			JSONObject info = JSONObject.parseObject(JSON.toJSONString(transaction));
			String blockHash = info.getString("blockhash");
			/*block*/
			//调用根据blockhash 获取块信息接口
			Map blockInfo = (Map)btService.getblock(blockHash);
			JSONObject block = JSONObject.parseObject(JSON.toJSONString(blockInfo));
			tranc.setBlockconfirmations(block.getString("confirmations"));
			tranc.setBlocktime(block.getString("time"));
			tranc.setBlockversion(block.getString("version"));
			tranc.setBlockheight(block.getString("height"));
			tranc.setBlockweight(block.getString("weight"));
			tranc.setPreviousblockhash(block.getString("previousblockhash"));
			tranc.setNextblockhash(block.getString("nextblockhash"));
			
			tranc.setTxId(info.getString("txid"));
			tranc.setHash(info.getString("hash"));
			tranc.setVersion(info.getString("version"));
			tranc.setSize(info.getString("size"));
			tranc.setVsize(info.getString("vsize"));
			tranc.setLocktime(info.getString("locktime"));
			tranc.setHex(info.getString("hex"));
			tranc.setBlockhash(blockHash);
			tranc.setConfirmations(info.getString("confirmations"));
			tranc.setTime(info.getString("time"));
			tranc.setBlocktime(info.getString("blocktime"));
			
			JSONArray vins = JSONArray.parseArray(info.getString("vin"));
			double sumvin = 0 ;
			List<String> formAddress = new ArrayList<>();
			for (int i = 0,len = vins.size(); i < len; i++) {
				JSONObject vin =  JSONObject.parseObject(vins.getString(i));
				String txid = vin.getString("txid");
				Integer vinN = vin.getInteger("vout");
				LOG.info("=== [BTC] search txid:{} trawtransaction! ===",txid);
				Map parentTransaction = (Map) btService.getTrawtransaction(txid, 1);
				JSONObject parentInfo = JSONObject.parseObject(JSON.toJSONString(parentTransaction));
				JSONArray vouts = JSONArray.parseArray(parentInfo.getString("vout"));
				for (int j = 0,leg = vouts.size(); j < leg; j++) {
					JSONObject vout = vouts.getJSONObject(j);
					Integer n = vout.getInteger("n");
					if(n == vinN){ //收款金额
						sumvin += vout.getDouble("value");
						JSONObject scriptPubKey = vout.getJSONObject("scriptPubKey");
						JSONArray addresses = scriptPubKey.getJSONArray("addresses");
						formAddress.add(addresses.getString(0));
					}
				}
			}
			tranc.setFrom(StringUtils.strip(formAddress.toString(),"[]"));
			
			JSONArray vouts = JSONArray.parseArray(info.getString("vout"));
			BigDecimal amount = null  ;
			double sumvout = 0;
			
			for (int i = 0,len = vouts.size(); i < len; i++) {
				JSONObject vout =  JSONObject.parseObject(vouts.getString(i));
				Integer n = vout.getInteger("n");
				JSONObject scriptPubKey = vout.getJSONObject("scriptPubKey");
				JSONArray addresses = scriptPubKey.getJSONArray("addresses");
				String receviceAddr = addresses.getString(0);
				if(n == 0){
					tranc.setTo(receviceAddr);
					amount = new BigDecimal(vout.getDouble("value"));
					 
				}
				 //找零金额
					sumvout  +=  vout.getDouble("value");
			}
			//手续费  sum(vin)- sum(vout)
			tranc.setFree(new BigDecimal(sumvin).subtract(new BigDecimal(sumvout)).setScale(8, BigDecimal.ROUND_HALF_UP));
			tranc.setAmount(amount.setScale(8, BigDecimal.ROUND_HALF_UP));
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
		return JSON.toJSON(tranc);
	}
