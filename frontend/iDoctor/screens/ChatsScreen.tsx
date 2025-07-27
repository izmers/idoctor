import { useFocusEffect } from "@react-navigation/native";
import {
  Button,
  FlatList,
  Modal,
  Pressable,
  StyleSheet,
  Text,
  View,
} from "react-native";
import { ChatStackParameterList } from "../App";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import {
  useCallback,
  useContext,
  useLayoutEffect,
  useRef,
  useState,
} from "react";
import { AuthContext } from "../store/auth-context";
import { FontAwesome } from "@expo/vector-icons";
import { CompatClient, Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { API_URL, request } from "../util/requests";
import Ionicons from "@expo/vector-icons/Ionicons";

type ChatsScreenNavigationProp = NativeStackNavigationProp<
  ChatStackParameterList,
  "ChatsView"
>;

type ChatsScreenProps = {
  navigation: ChatsScreenNavigationProp;
};

interface User {
  username: string;
  email: string;
  city: string;
  fullName: string;
}
interface Doctor {
  id: number;
  user: User;
  doctorType: string;
  phoneNumber: string;
  street: string;
}

interface ChatChannel {
  id: number;
  patient: User;
  doctor: Doctor;
  lastMessage: string;
  dateOfLastMessage: string;
  chatChannelStatus: string;
  doctorNote: string;
}

export default function ChatsScreen({ navigation }: ChatsScreenProps) {
  const [_, setIsLoading] = useState<boolean>(false);
  const [counter, setCounter] = useState<number>(0);
  const [reload, setReload] = useState<boolean>(false);
  const [currentSelectedChatChannel, setCurrentSelectedChatChannel] =
    useState<ChatChannel>();
  const [messageModalOpen, setMessageModalOpen] = useState<boolean>(false);
  const [chatChannels, setChatChannels] = useState<ChatChannel[]>([]);
  const stompClientRef = useRef<CompatClient | null>(null);
  const authCtx = useContext(AuthContext);

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

  async function onMessageReceived() {
    setCounter((prev) => prev + 1);
  }

  function onError(error?: string) {
    console.error("An error occurred with the WebSocket connection:", error);
  }

  useFocusEffect(
    useCallback(() => {
      if (authCtx.isDoctor) {
        connect();

        return () => {
          stompClientRef.current?.disconnect(() => {});
        };
      }
    }, [])
  );

  useFocusEffect(
    useCallback(() => {
      async function loadChatChannels() {
        await request<ChatChannel[]>({
          request: `${API_URL}/api/chat-channel/${
            authCtx.isDoctor ? "by-doctor" : "by-patient"
          }/${authCtx.username}`,
          token: authCtx.token,
          setIsLoading: setIsLoading,
          onSuccess: (resData: ChatChannel[]) => setChatChannels(resData),
        });
      }

      loadChatChannels();
    }, [])
  );

  useLayoutEffect(() => {
    if (authCtx.isDoctor) {
      navigation.setOptions({
        headerRight: () => (
          <Pressable
            style={[styles.slotBtn, { position: "relative" }]}
            onPress={() => {
              setCounter(0);
              navigation.navigate("ChatRequests");
            }}
          >
            {counter > 0 && (
              <View
                style={{
                  position: "absolute",
                  top: -4.1,
                  right: -10.1,
                  backgroundColor: "red",
                  borderRadius: 50,
                  width: 20,
                  height: 20,
                  justifyContent: "center",
                  alignItems: "center",
                }}
              >
                <Text style={{ color: "white" }}>{counter}</Text>
              </View>
            )}

            <Text style={{ color: "white" }}>Requests</Text>
          </Pressable>
        ),
        headerLeft: () => (
          <View style={{ width: "30%" }}>
            <Ionicons
              name="reload-circle-sharp"
              size={35}
              color="black"
              style={{ marginBottom: 2.5 }}
              onPress={() => setReload((prev) => !prev)}
            />
          </View>
        ),
      });
    }
  }, [counter]);

  return (
    <View style={styles.container}>
      <Modal
        animationType="slide"
        transparent={true}
        visible={messageModalOpen}
      >
        <View style={styles.messageModalContainer}>
          <View style={styles.messageModalView}>
            <Text>{currentSelectedChatChannel?.doctorNote}</Text>
            <Button title="Close" onPress={() => setMessageModalOpen(false)} />
          </View>
        </View>
      </Modal>

      <FlatList
        style={{ width: "90%", marginTop: 15 }}
        data={
          authCtx.isDoctor
            ? chatChannels.filter(
                (channel) => channel.chatChannelStatus === "ACTIVE"
              )
            : chatChannels
        }
        renderItem={({ item }) => (
          <Pressable
            style={{
              padding: 10,
              height: 60,
              justifyContent: "space-between",
              borderBottomWidth: 0.18,
              borderBottomColor: "grey",
            }}
            disabled={item.chatChannelStatus === "INACTIVE"}
            onPress={() =>
              navigation.navigate("PrivateChat", {
                username: authCtx.isDoctor
                  ? item.patient.username
                  : item.doctor.user.username,
                channelId: item.id,
              })
            }
          >
            <View
              style={{
                flexDirection: "row",
                justifyContent: "space-between",
              }}
            >
              {authCtx.isDoctor ? (
                <Text>{item.patient.fullName}</Text>
              ) : (
                <View
                  style={{
                    flexDirection: "row",
                    gap: 10,
                    justifyContent: "center",
                    alignItems: "center",
                  }}
                >
                  <Text>Dr. {item.doctor.user.fullName}</Text>
                  {item.chatChannelStatus === "INACTIVE" &&
                    (messageModalOpen &&
                    currentSelectedChatChannel?.id === item.id ? (
                      <FontAwesome
                        name="envelope-open"
                        size={20}
                        color="black"
                        onPress={() => setMessageModalOpen(false)}
                      />
                    ) : (
                      <FontAwesome
                        name="envelope"
                        size={20}
                        color="black"
                        onPress={() => {
                          setMessageModalOpen(true);
                          setCurrentSelectedChatChannel(item);
                        }}
                      />
                    ))}
                </View>
              )}
              <Text style={{ color: "grey", fontSize: 12 }}>
                {item.dateOfLastMessage
                  ? item.dateOfLastMessage.slice(11, 16)
                  : ""}
              </Text>
            </View>

            <Text style={{ color: "grey", fontSize: 12 }}>
              {item.lastMessage ?? ""}
            </Text>
          </Pressable>
        )}
        keyExtractor={(item) => item.id.toString()}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "white",
    alignItems: "center",
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
  slotBtn: {
    backgroundColor: "black",
    width: 90,
    height: 30,
    marginHorizontal: 15,
    marginBottom: 5,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 25,
  },
});
