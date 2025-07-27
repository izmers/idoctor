import { CompatClient, Stomp } from "@stomp/stompjs";
import { useCallback, useContext, useEffect, useRef, useState } from "react";
import {
  Button,
  FlatList,
  Keyboard,
  Modal,
  StyleSheet,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import SockJS from "sockjs-client";
import { AuthContext } from "../store/auth-context";
import { useFocusEffect } from "@react-navigation/native";
import LoadingComponent from "../components/LoadingComponent";
import { FontAwesome } from "@expo/vector-icons";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { API_URL, request } from "../util/requests";

interface ChatRequest {
  id: number;
  userUsername: string;
  doctorUsername: string;
  userNote: string;
  doctorNote: string;
  chatRequestStatus: string;
}

export default function ChatRequestsScreen() {
  const [requests, setRequests] = useState<ChatRequest[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [messageModalOpen, setMessageModalOpen] = useState<boolean>(false);
  const [noteModal, setNoteModal] = useState<boolean>(false);
  const [declineModal, setDeclineModal] = useState<boolean>(false);
  const [selectedRequest, setSelectedRequest] = useState<
    ChatRequest | undefined
  >(undefined);
  const [currentDecision, setCurrentDecision] = useState<string>("");
  const [currentChatRequest, setCurrentChatRequest] = useState<ChatRequest>();
  const [optionalText, setOptionalText] = useState<string>("");
  const authCtx = useContext(AuthContext);
  const stompClientRef = useRef<CompatClient | null>(null);

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
      `/user/${authCtx.username}/queue/requests`,
      onMessageReceived
    );
  }

  async function onMessageReceived(payload: { body: string }) {
    const request = JSON.parse(payload.body);
    setRequests((prevRequests) => [...prevRequests, request]);
  }

  function onError(error?: string) {
    console.error("An error occurred with the WebSocket connection:", error);
  }

  async function fetchHistory() {
    await request<ChatRequest[]>({
      request: `${API_URL}/api/chat-request/by-doctor/${authCtx.username}`,
      token: authCtx.token,
      setIsLoading: setIsLoading,
      onSuccess: (resData: ChatRequest[]) => {
        const chatRequests: ChatRequest[] = resData.map((item: any) => ({
          id: item.id,
          userUsername: item.user.username,
          doctorUsername: item.doctor.user.username,
          userNote: item.userNote,
          doctorNote: item.doctorNote,
          chatRequestStatus: item.chatRequestStatus,
        }));

        setRequests(chatRequests);
      },
    });
  }

  useFocusEffect(
    useCallback(() => {
      fetchHistory();
      return () => {};
    }, [currentChatRequest])
  );

  useEffect(() => {
    connect();

    return () => {
      stompClientRef.current?.disconnect();
    };
  }, []);

  async function handleSendDecision() {
    setIsLoading(true);

    const message: ChatRequest = {
      id: currentChatRequest!.id,
      userUsername: currentChatRequest!.userUsername,
      doctorUsername: currentChatRequest!.doctorUsername,
      userNote: currentChatRequest!.userNote,
      doctorNote: optionalText,
      chatRequestStatus: currentChatRequest!.chatRequestStatus,
    };

    await request({
      method: "PUT",
      strBody: JSON.stringify(message),
      request: `${API_URL}/api/chat-request/${currentDecision}/${
        currentChatRequest!.id
      }`,
      token: authCtx.token,
      setIsLoading: setIsLoading,
      onSuccess: (_) => {
        setCurrentDecision("");
        setCurrentChatRequest(undefined);
        setNoteModal(false);
        setDeclineModal(false);
        setOptionalText("");
      },
      onError: () => {
        setCurrentDecision("");
        setCurrentChatRequest(undefined);
        setNoteModal(false);
        setDeclineModal(false);
        setOptionalText("");
      },
    });
  }

  if (isLoading) {
    return <LoadingComponent />;
  }

  return (
    <View style={styles.container}>
      <Modal animationType="slide" transparent={true} visible={noteModal}>
        <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
          <KeyboardAwareScrollView
            contentContainerStyle={styles.messageModalContainer}
          >
            <View style={styles.acceptModalView}>
              <Text style={{ fontSize: 15 }}>
                Do you want to accept the chat request?
              </Text>
              <View style={{ flexDirection: "row" }}>
                <Button title="Yes" onPress={handleSendDecision} />
                <Button
                  title="No"
                  color="red"
                  onPress={() => {
                    setNoteModal(false);
                  }}
                />
              </View>
            </View>
          </KeyboardAwareScrollView>
        </TouchableWithoutFeedback>
      </Modal>

      <Modal animationType="slide" transparent={true} visible={declineModal}>
        <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
          <KeyboardAwareScrollView
            contentContainerStyle={styles.messageModalContainer}
          >
            <View style={styles.messageModalView}>
              <TextInput
                style={{
                  borderWidth: 1,
                  borderRadius: 10,
                  width: "95%",
                  height: "50%",
                  borderColor: "grey",
                  padding: 10,
                  marginTop: 10,
                }}
                value={optionalText}
                onChangeText={(value) => setOptionalText(value)}
                multiline={true}
                numberOfLines={5}
                placeholder="Add a note"
                placeholderTextColor="grey"
                returnKeyType="default"
                autoCorrect={false}
                autoComplete="off"
                autoCapitalize="none"
              />
              <View style={{ flexDirection: "row" }}>
                <Button title="Send" onPress={handleSendDecision} />
                <Button
                  title="Close"
                  color="red"
                  onPress={() => {
                    setOptionalText("");
                    setDeclineModal(false);
                  }}
                />
              </View>
            </View>
          </KeyboardAwareScrollView>
        </TouchableWithoutFeedback>
      </Modal>

      <Modal
        animationType="slide"
        transparent={true}
        visible={messageModalOpen}
      >
        <View style={styles.messageModalContainer}>
          <View style={styles.messageModalView}>
            <Text>{selectedRequest?.userNote}</Text>
            <Button title="Close" onPress={() => setMessageModalOpen(false)} />
          </View>
        </View>
      </Modal>

      <FlatList
        style={{ width: "100%", marginTop: 10 }}
        data={requests}
        renderItem={({ item }) => (
          <View
            style={{
              backgroundColor: "#f4f5f2",
              marginBottom: 25,
              borderRadius: 15,
              height: 100,
              alignItems: "center",
              padding: 10,
              width: "80%",
              alignSelf: "center",
              justifyContent: "space-around",
              shadowColor: "#000",
              shadowOffset: { width: 0, height: 4 },
              shadowOpacity: 0.3,
              shadowRadius: 4,
              elevation: 5,
            }}
          >
            <View
              style={{
                flexDirection: "row",
                justifyContent: "space-evenly",
              }}
            >
              <Text style={{ fontSize: 20, marginRight: 10 }}>
                {item.userUsername}
              </Text>
              {messageModalOpen &&
              selectedRequest?.userUsername === item.userUsername ? (
                <FontAwesome
                  name="envelope-open"
                  size={24}
                  color="black"
                  onPress={() => setMessageModalOpen(false)}
                />
              ) : (
                <FontAwesome
                  name="envelope"
                  size={24}
                  color="black"
                  onPress={() => {
                    setSelectedRequest(item);
                    setMessageModalOpen(true);
                  }}
                />
              )}
            </View>
            <View style={{ flexDirection: "row" }}>
              <Button
                title="Accept"
                onPress={() => {
                  setNoteModal(true);
                  setCurrentDecision("accept");
                  setCurrentChatRequest(item);
                }}
              />
              <Button
                title="Decline"
                color="red"
                onPress={() => {
                  setDeclineModal(true);
                  setCurrentDecision("decline");
                  setCurrentChatRequest(item);
                }}
              />
            </View>
          </View>
        )}
        keyExtractor={(item) => item.id.toString()}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "white",
  },

  messageModalContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(230, 237, 232, 0.5)",
  },

  messageModalView: {
    backgroundColor: "white",
    alignItems: "center",
    justifyContent: "space-between",
    borderRadius: 15,
    padding: 10,
    width: "80%",
    height: "25%",
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  acceptModalView: {
    backgroundColor: "white",
    alignItems: "center",
    justifyContent: "space-between",
    borderRadius: 15,
    padding: 10,
    width: "80%",
    height: "10%",
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
});
