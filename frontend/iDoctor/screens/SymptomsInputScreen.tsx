import {
  FlatList,
  Keyboard,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import ScreenTitleComponent from "../components/ScreenTitleComponent";
import AntDesign from "@expo/vector-icons/AntDesign";
import { useContext, useRef, useState } from "react";
import { API_URL } from "../util/requests";
import { AuthContext } from "../store/auth-context";
import {
  CompositeNavigationProp,
  useNavigation,
} from "@react-navigation/native";
import { DoctorsStackParameterList, RootStackParamList } from "../App";
import { BottomTabNavigationProp } from "@react-navigation/bottom-tabs";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import LoadingComponent from "../components/LoadingComponent";

interface User {
  email: string;
  username: string;
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

interface Message {
  id: number;
  userText: string;
  botText: string;
  recommendedDoctorTypes: string[];
  recommendedDoctors: Doctor[];
}

type AppNavigationProp = CompositeNavigationProp<
  BottomTabNavigationProp<RootStackParamList, "Main">,
  NativeStackNavigationProp<DoctorsStackParameterList, "DoctorSearch">
>;

export default function SymptomsInputScreen() {
  const [problemText, setProblemText] = useState<string>("");
  const [messages, setMessages] = useState<Message[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const flatListRef = useRef<FlatList<Message>>(null);
  const authCtx = useContext(AuthContext);
  const navigation = useNavigation<AppNavigationProp>();

  async function handleSendText() {
    try {
      Keyboard.dismiss();
      setIsLoading(true);
      const mlLayerResponse = await fetch("http://127.0.0.1:5003/chatbot", {
        method: "POST",
        body: JSON.stringify({
          message: problemText,
        }),
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!mlLayerResponse.ok) {
        throw new Error(
          "error with response status code: " + mlLayerResponse.status
        );
      }

      const mlResData = await mlLayerResponse.json();

      let keysArray: string[] = [];
      let uniqueValuesArray: string[] = [];

      if (
        typeof mlResData.reply === "object" &&
        !JSON.stringify(mlResData.reply).includes("An error occurred")
      ) {
        keysArray = Object.keys(mlResData.reply);
        uniqueValuesArray = Array.from(new Set(Object.values(mlResData.reply)));
      }

      const response = await fetch(`${API_URL}/api/doctor/recommended`, {
        method: "POST",
        body: JSON.stringify(uniqueValuesArray),
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + authCtx.token,
        },
      });

      const resData: Doctor[] = await response.json();

      const resultString = `These are the possible problems you might have: ${keysArray.join(
        ", "
      )}.`;

      const newMessage: Message = {
        id: messages.length + 1,
        userText: problemText,
        botText: `${resultString}...Here are some recommended doctors:`,
        recommendedDoctorTypes: uniqueValuesArray,
        recommendedDoctors: resData,
      };

      setMessages((prev) => [...prev, newMessage]);
      setProblemText("");
      setIsLoading(false);
    } catch (error) {
      if (error instanceof Error) {
        console.error(error.message);
      } else {
        console.error("An unknown error occurred", error);
      }
      setIsLoading(false);
    }
  }

  const scrollToBottom = () => {
    flatListRef.current?.scrollToEnd({ animated: false });
  };

  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <View style={styles.container}>
        <ScreenTitleComponent isMainTitle={false} />
        <View style={{ width: "90%", alignItems: "center" }}>
          <MaterialIcons
            name="logout"
            size={24}
            color="black"
            onPress={() => authCtx.logout()}
          />
        </View>
        <View
          style={{
            flexDirection: "row",
            width: "95%",
            alignItems: "center",
            justifyContent: "space-evenly",
            marginTop: 30,
          }}
        >
          <TextInput
            style={{
              backgroundColor: "#e1e3dc",
              borderRadius: 25,
              padding: 14,
              paddingTop: 17,
              width: "85%",
              maxHeight: 200,
            }}
            value={problemText}
            onChangeText={(value) => setProblemText(value)}
            multiline
            numberOfLines={5}
            scrollEnabled
            placeholder="Describe your problem here"
            placeholderTextColor="grey"
          />
          {problemText.length !== 0 && (
            <Pressable
              style={{
                backgroundColor: "#ecede8",
                borderRadius: 25,
                alignItems: "center",
                justifyContent: "center",
                width: 40,
                height: 40,
                opacity: problemText.length === 0 ? 0.2 : 1,
              }}
              onPress={handleSendText}
            >
              <AntDesign name="arrowup" size={24} color="black" />
            </Pressable>
          )}
        </View>

        <Pressable
          style={{
            backgroundColor: "black",
            width: "70%",
            borderRadius: 25,
            marginTop: 10,
            justifyContent: "center",
            alignItems: "center",
            height: 30,
            opacity: messages.length === 0 ? 0.5 : 1,
          }}
          disabled={messages.length === 0}
          onPress={() => setMessages([])}
        >
          <MaterialIcons name="cleaning-services" size={24} color="white" />
        </Pressable>

        <FlatList
          data={messages}
          ref={flatListRef}
          style={{ width: "100%", marginTop: 10 }}
          renderItem={({ item }) => (
            <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
              <View style={{ width: "100%" }}>
                <View
                  style={{
                    backgroundColor: "#f4f5f2",
                    borderRadius: 25,
                    marginTop: 25,
                    alignSelf: "flex-end",
                    marginRight: 10,
                    padding: 10,
                    maxWidth: "50%",
                  }}
                >
                  <Text>{item.userText}</Text>
                </View>

                <View
                  style={{
                    backgroundColor: "#ecede8",
                    borderRadius: 25,
                    marginTop: 25,
                    alignSelf: "flex-start",
                    marginLeft: 10,
                    padding: 10,
                    maxWidth: "50%",
                    marginBottom: 10,
                  }}
                >
                  <Text>{item.botText}</Text>
                  <Pressable
                    style={{
                      flexDirection: "row",
                      backgroundColor: "#9feffc",
                      borderRadius: 25,
                      justifyContent: "space-between",
                      alignItems: "center",
                      width: "50%",
                      padding: 5,
                      margin: 5,
                    }}
                    onPress={() =>
                      navigation.navigate("Doctors", {
                        screen: "DoctorSearch",
                        params: {
                          recommendedDoctors: item.recommendedDoctors,
                        },
                      })
                    }
                  >
                    <AntDesign name="arrowright" size={20} color="black" />
                    <Text>Doctors</Text>
                  </Pressable>
                </View>
              </View>
            </TouchableWithoutFeedback>
          )}
          onContentSizeChange={scrollToBottom}
          keyExtractor={(item) => item.id.toString()}
        />
      </View>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    backgroundColor: "white",
  },
});
