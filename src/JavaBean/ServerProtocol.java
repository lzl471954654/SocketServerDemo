package JavaBean;

public interface ServerProtocol {
    /*
    * HEART_BEAT            心跳
    * MAKE_HOLE             打洞
    * HOLE_SUCCESS          打洞成功
    * HOLE_FAILED           打洞失败
    * ONLINE                上线
    * OFFLINE               离线
    * CONNECTED_TO_USER     连接--用户
    * CONNECTED_SUCCESS     连接用户成功
    * CONTROL               控制端
    * BE_CONTROLLED         被控制端
    * SYMMETRIC_NAT_MODE    对等NAT模式
    * ASYMMETRIC_NAT_MODE   不对等NAT模式
    * UDP_MODE              UDP模式
    * TCP_MODE              TCP模式
    * */
    public static final String HEATR_BEAT = "|BEAT|";
    public static final String MAKE_HOLE = "|HOLE|";
    public static final String HOLE_SUCCESS = "|HOLE@SUCCESS|";
    public static final String HOLE_FAILED = "|HOLE@FAIL|";
    public static final String ONLINE = "|ONLINE|";
    public static final String OFFLINE = "|OFFLINE|";
    public static final String CONNECTED_TO_USER = "|CONNECTED@TO@USER|";
    public static final String CONNECTED_SUCCESS = "|CONNECTED@SUCCESS|";
    public static final String CONNECTED_FAILED = "|CONNECTED@FAILED|";
    public static final String CONTROL = "|CONTROL|";
    public static final String BE_CONTROLLED = "|BE@CONTROLLED|";
    public static final String SYMMETRIC_NAT_MODE = "|SYMMETRIC@NAT|";
    public static final String ASYMMETRIC_NAT_MODE = "|ASYMMETRIC@NAT|";
    public static final String UDP_MODE = "|UDP@MODE|";
    public static final String TCP_MODE = "|TCP@MODE|";
    public static final String ERROR = "|ERROR|";
    public static final String NORMAL_MSG = "|NORMAL@MSG|";
    public static final String ONLINE_SUCCESS = "|ONLINE@SUCCESS|";
    public static final String ONLINE_FAILED = "|ONLINE_FAILED|";
    public static final String END_FLAG = "@@|END@FLAG|@@";


    public static final String COMMAND = "|COMMAND|";
    public static final String COMMAND_RESULT = "|COMMAND@RESULT|";

    /*
    * 绑定标志
    * 表示 被控端控制端之间的通道是否打通
    * */

    public static final String BIND_SUCCESS = "|BIND@SUCCESS|";
    public static final String UNBIND_ERROR = "|UNBIND@ERROR|";

    /*
    * 文件发送格式
    * 以 发送文件标志，或文件列表标志开头，结束标志结尾，END_FLAG
    * 格式为 开头标志+json格式的文件描述或文件列表+结束标志
    * */
    public static final String FILE_LIST_FLAG = "|FILE@LIST@FLAG|";     //发送文件列表标志
    public static final String FILE_READY = "|FILE@READY|"; //已经准备好接收文件
    String FILE_NOT_READY = "|FILE@NOT@READY|";


    /*
    * 发送截图
    * */
    String PIC_SEND = "|PIC@SEND|";
}
