//交易信息
@Data
public class BtcTransactionInfo implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private String txId; //交易txid
	private String hash; //hash
	private String blockheight;//区块高度
	private String blockweight; //区块宽度
	private String blockconfirmations;//区块确认数
	private String blockversion;
	private String previousblockhash; //上一个区块的块hash
	private String nextblockhash; //下一个块hash
	private String version;
	private String size;
	private String vsize;
	private String locktime;
	private String hex; //hex
	private String blockhash;
	private String confirmations; //确认次数
	private String time;
	private String blocktime;
	private BigDecimal amount; //金额
	private BigDecimal free; //手续费
	private String from; //发送方
	private String to; //接收方
	
	 
}
