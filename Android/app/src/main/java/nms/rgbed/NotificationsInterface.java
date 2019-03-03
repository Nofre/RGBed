package nms.rgbed;

public interface NotificationsInterface {
    void reconnect();

    void showConnecting(boolean show);

    void showConnectButton();

    void showToast(String msg);
}
