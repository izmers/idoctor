import { RouteProp, useFocusEffect, useRoute } from "@react-navigation/native";
import {
  Button,
  FlatList,
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import { ChatStackParameterList } from "../App";
import {
  useCallback,
  useContext,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import { AuthContext } from "../store/auth-context";
import { API_URL, request } from "../util/requests";
import { CompatClient, Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import LoadingComponent from "../components/LoadingComponent";
import MessageBoxComponent from "../components/MessageBoxComponent";
import Ionicons from "@expo/vector-icons/Ionicons";
import { NativeStackScreenProps } from "@react-navigation/native-stack";

interface ChatText {
  id: number;
  senderCred: string;
  recipientCred: string;
  content: string;
  channelId: number;
  created: Date;
}

type PrivateChatRouteProp = RouteProp<ChatStackParameterList, "PrivateChat">;
type PrivateChatScreenProps = NativeStackScreenProps<
  ChatStackParameterList,
  "PrivateChat"
>;

export default function PrivateChatScreen({
  navigation,
}: PrivateChatScreenProps) {
  const [messages, setMessages] = useState<ChatText[]>([]);
  const [newMessage, setNewMessage] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const authCtx = useContext(AuthContext);
  const route = useRoute<PrivateChatRouteProp>();
  const stompClientRef = useRef<CompatClient | null>(null);
  const flatListRef = useRef<FlatList<ChatText>>(null);

  useLayoutEffect(() => {
    navigation.setOptions({
      headerTitle: route.params.username,
      headerTintColor: "black",
    });
  }, []);

  function connect(): void {
    const socket: WebSocket = new SockJS(`${API_URL}/ws`);
    const stompClient = Stomp.over(socket);
    stompClientRef.current = stompClient;

    stompClient.connect(
      {},
      () => {
        onConnected();
      },
      onError
    );
  }

  function onConnected() {
    stompClientRef.current?.subscribe(
      `/user/${authCtx.username}/queue/messages`,
      onMessageReceived
    );
  }

  async function onMessageReceived(payload: { body: string }) {
    const message = JSON.parse(payload.body);
    setMessages((prevMessages) => [...prevMessages, message]);
  }

  function onError(error?: string) {
    console.error("An error occurred with the WebSocket connection:", error);
  }

  const scrollToBottom = () => {
    flatListRef.current?.scrollToEnd({ animated: false });
  };

  async function fetchHistory() {
    await request<ChatText[]>({
      request: `${API_URL}/api/chat-text/by-channel/${route.params.channelId}`,
      token: authCtx.token,
      setIsLoading: setIsLoading,
      onSuccess: (resData: ChatText[]) => {
        const chatTexts: ChatText[] = resData.map((item: any) => ({
          id: item.id,
          senderCred: item.sender.username,
          recipientCred: item.recipient.username,
          content: item.content,
          channelId: item.chatChannel.id,
          created: item.timestamp,
        }));

        setMessages(chatTexts);
      },
    });
  }

  useFocusEffect(
    useCallback(() => {
      fetchHistory();
      return () => {};
    }, [])
  );

  useEffect(() => {
    connect();
    scrollToBottom();

    return () => {
      stompClientRef.current?.disconnect(() => {});
    };
  }, []);

  const handleSendMessage = async (): Promise<void> => {
    const senderCred = authCtx.username;
    const recipientCred = route.params.username;
    const message: ChatText = {
      id: messages.length + 1,
      senderCred,
      recipientCred,
      content: newMessage,
      channelId: route.params.channelId,
      created: new Date(),
    };

    stompClientRef.current?.send("/app/chat", {}, JSON.stringify(message));
    if (stompClientRef.current) {
      stompClientRef.current.onStompError = (frame) => {
        console.error("STOMP error:", frame.headers["message"]);
        console.error("Details:", frame.body);
      };
    } else {
      console.error("STOMP client is not initialized.");
    }

    setNewMessage("");
    setMessages((prev) => [...prev, message]);
  };

  if (isLoading) {
    return <LoadingComponent />;
  }

  return (
    <KeyboardAvoidingView
      style={{ flex: 1 }}
      behavior={Platform.OS === "ios" ? "padding" : undefined}
      keyboardVerticalOffset={100}
    >
      <View style={styles.container}>
        <FlatList
          ref={flatListRef}
          data={messages}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <MessageBoxComponent
              text={item.content}
              isSentMessage={item.senderCred === authCtx.username}
            />
          )}
          onContentSizeChange={scrollToBottom}
        />
        <View style={styles.inputContainer}>
          <TextInput
            style={styles.input}
            value={newMessage}
            onChangeText={setNewMessage}
            multiline
          />
          {newMessage.length !== 0 && (
            <Ionicons
              name="send"
              size={24}
              color="#0078fe"
              onPress={handleSendMessage}
            />
          )}
        </View>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 10,
    backgroundColor: "white",
  },
  message: {
    padding: 10,
    borderRadius: 5,
    marginBottom: 5,
    backgroundColor: "#f0f0f0",
  },
  inputContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginTop: 10,
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 25,
    padding: 10,
    marginRight: 10,
  },
});
