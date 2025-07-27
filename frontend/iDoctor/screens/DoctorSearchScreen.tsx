import { useCallback, useContext, useEffect, useRef, useState } from "react";
import {
  Button,
  FlatList,
  Keyboard,
  Modal,
  Pressable,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import { AuthContext } from "../store/auth-context";
import LoadingComponent from "../components/LoadingComponent";
import {
  NavigationProp,
  RouteProp,
  useFocusEffect,
  useNavigation,
  useRoute,
} from "@react-navigation/native";
import { DoctorsStackParameterList } from "../App";
import FilterByCountryModelComponent from "../components/FilterByCountryModalComponent";
import FilterByCityModalComponent from "../components/FilterByCityModalComponent";
import FilterByDoctorTypeModalComponent from "../components/FilterByDoctorTypeModalComponent";
import Feather from "@expo/vector-icons/Feather";
import Entypo from "@expo/vector-icons/Entypo";
import { CompatClient, Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { API_URL, request } from "../util/requests";

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

interface ChatRequest {
  userUsername: string;
  doctorUsername: string;
  userNote: string;
  doctorNote: string;
}

type DoctorSearchScreenRouteProp = RouteProp<
  DoctorsStackParameterList,
  "DoctorSearch"
>;

export default function DoctorSearchScreen() {
  const [searchFocused, setSearchFocused] = useState<boolean>(false);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [query, setQuery] = useState<string>("");
  const [filteredData, setFilteredData] = useState<Doctor[]>([]);
  const [filterDismiss, setFilterDismiss] = useState<boolean>(true);
  const [allDoctors, setAllDoctors] = useState<Doctor[]>([]);
  const [filteredCountry, setFilteredCountry] = useState<string>("");
  const [filteredCity, setFilteredCity] = useState<string>("");
  const [filteredDoctorType, setFilteredDoctorType] = useState<string>("");
  const [countryModalVisible, setCountryModalVisible] =
    useState<boolean>(false);
  const [cityModalVisible, setCityModalVisible] = useState<boolean>(false);
  const [doctorTypeModalVisible, setDoctorTypeModalVisible] =
    useState<boolean>(false);
  const [messageModalOpen, setMessageModelOpen] = useState<boolean>(false);
  const [optionalText, setOptionalText] = useState<string>("");
  const [selectedDoctor, setSelectedDoctor] = useState<Doctor>();
  const [requestErrorExists, setRequestErrorExists] = useState<boolean>(false);
  const authCtx = useContext(AuthContext);
  const navigation = useNavigation<NavigationProp<DoctorsStackParameterList>>();
  const stompClientRef = useRef<CompatClient | null>(null);
  const route = useRoute<DoctorSearchScreenRouteProp>();

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

  async function onMessageReceived(payload: { body: string }) {
    const message = JSON.parse(payload.body);
    setRequestErrorExists(true);
  }

  function onConnected() {
    stompClientRef.current?.subscribe(
      `/user/${authCtx.username}/queue/errors`,
      onMessageReceived
    );
  }

  function handleSendRequest() {
    setRequestErrorExists(false);
    const userUsername = authCtx.username;
    const doctorUsername = selectedDoctor!.user.username;
    const message: ChatRequest = {
      userUsername,
      doctorUsername,
      userNote: optionalText,
      doctorNote: "",
    };

    stompClientRef.current?.send("/app/request", {}, JSON.stringify(message));
    if (stompClientRef.current) {
      stompClientRef.current.onStompError = (frame) => {
        console.error("STOMP error:", frame.headers["message"]);
        console.error("Details:", frame.body);
      };
    } else {
      console.error("STOMP client is not initialized.");
    }
    setMessageModelOpen(false);
    setOptionalText("");
  }

  function onError(error?: string) {
    console.error("An error occurred with the WebSocket connection:", error);
  }

  async function handleApplyFilter() {
    setIsLoading(true);
    await request<Doctor[]>({
      request: `${API_URL}/api/doctor/filter?country=${filteredCountry}&city=${filteredCity}&type=${filteredDoctorType}`,
      token: authCtx.token,
      setIsLoading: setIsLoading,
      onSuccess: (resData: Doctor[]) => {
        setDoctors(resData);
        setFilteredCountry("");
        setFilteredCity("");
        setFilteredDoctorType("");
      },
    });
  }

  function handleSearch(text: string) {
    setQuery(text);
    if (text) {
      setFilterDismiss(false);
      const suggestions = allDoctors.filter((item) =>
        item.user.fullName.toLowerCase().includes(text.toLowerCase())
      );
      setFilteredData(suggestions);
    } else {
      setFilteredData([]);
    }
  }

  async function handleSearchSubmit() {
    setFilteredData([]);
    await request<Doctor[]>({
      request: `${API_URL}/api/doctor/by-name/${query}`,
      token: authCtx.token,
      setIsLoading: setIsLoading,
      onSuccess: (resData: Doctor[]) => {
        setDoctors(resData);
        setQuery("");
        Keyboard.dismiss();
      },
    });
  }

  async function loadNearbyDoctors() {
    await request<Doctor[]>({
      request: `${API_URL}/api/doctor/nearby`,
      token: authCtx.token,
      setIsLoading: setIsLoading,
      onSuccess: (resData: Doctor[]) => {
        setDoctors(resData);
      },
    });
  }

  useEffect(() => {
    async function loadAllDoctors() {
      await request<Doctor[]>({
        request: `${API_URL}/api/doctor/all`,
        token: authCtx.token,
        setIsLoading: setIsLoading,
        onSuccess: (resData: Doctor[]) => {
          setAllDoctors(resData);
        },
      });
    }

    loadAllDoctors();

    if (!route.params || route.params.recommendedDoctors.length === 0) {
      loadNearbyDoctors();
    } else {
      setDoctors(route.params.recommendedDoctors);
    }
  }, [route.params]);

  useFocusEffect(
    useCallback(() => {
      connect();
      return () => {
        stompClientRef.current?.disconnect();
      };
    }, [])
  );

  if (isLoading) {
    return <LoadingComponent />;
  }

  return (
    <SafeAreaView style={styles.container}>
      <Modal
        animationType="slide"
        transparent={true}
        visible={messageModalOpen}
      >
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
                <Button title="Send" onPress={handleSendRequest} />
                <Button
                  title="Cancel"
                  color="red"
                  onPress={() => {
                    setMessageModelOpen(false);
                    setOptionalText("");
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
        visible={requestErrorExists}
      >
        <View style={styles.messageModalContainer}>
          <View style={styles.errorMessageModalView}>
            <Text>You have already sent a chat request for this doctor.</Text>
            <Button
              title="Ok"
              onPress={() => {
                setRequestErrorExists(false);
                setOptionalText("");
              }}
            />
          </View>
        </View>
      </Modal>

      <FilterByCountryModelComponent
        modalVisible={countryModalVisible}
        handleCloseModal={() => setCountryModalVisible(false)}
        setFilteredCountry={setFilteredCountry}
      />

      <FilterByCityModalComponent
        modalVisible={cityModalVisible}
        handleCloseModal={() => setCityModalVisible(false)}
        setFilteredCity={setFilteredCity}
      />

      <FilterByDoctorTypeModalComponent
        modalVisible={doctorTypeModalVisible}
        handleCloseModal={() => setDoctorTypeModalVisible(false)}
        setFilteredDoctorType={setFilteredDoctorType}
      />
      <View
        style={{
          flexDirection: "row",
          justifyContent: "space-evenly",
          marginTop: 20,
          alignItems: "center",
          width: "100%",
          paddingLeft: 10,
          paddingRight: 10,
        }}
      >
        <TextInput
          style={{
            width: searchFocused ? "75%" : "90%",
            height: 45,
            padding: 10,
            borderWidth: 1,
            borderRadius: 25,
          }}
          placeholder="Search for doctors"
          returnKeyType="default"
          autoCorrect={false}
          autoComplete="off"
          autoCapitalize="none"
          onSubmitEditing={() => {
            Keyboard.dismiss();
            setFilterDismiss(true);
          }}
          onFocus={() => {
            setSearchFocused(true);
            setFilterDismiss(false);
          }}
          onBlur={() => {
            setSearchFocused(false);
          }}
          onChangeText={handleSearch}
          value={query}
        />

        {searchFocused && (
          <Pressable
            style={({ pressed }) =>
              pressed ? [styles.pressed, styles.searchBtn] : styles.searchBtn
            }
            onPress={handleSearchSubmit}
          >
            <Text>Search</Text>
          </Pressable>
        )}
      </View>

      <View style={{ width: "95%" }}>
        {filteredData.length > 0 && !filterDismiss && (
          <FlatList
            style={styles.suggestionsList}
            data={filteredData}
            keyExtractor={(item) => item.id.toString()}
            renderItem={({ item }) => (
              <Pressable
                style={styles.suggestionItem}
                onPress={() => {
                  setQuery(item.user.fullName);
                  setFilteredData([]);
                }}
              >
                <Text>Dr. {item.user.fullName}</Text>
              </Pressable>
            )}
            keyboardShouldPersistTaps="always"
          />
        )}
      </View>

      <View
        style={{
          flexDirection: "row",
          justifyContent: "space-evenly",
          width: "90%",
          marginTop: 15,
        }}
      >
        <Pressable
          style={styles.filterBtn}
          onPress={() => setCountryModalVisible(true)}
        >
          <Text style={{ color: "white" }}>Country</Text>
        </Pressable>
        <Pressable
          style={styles.filterBtn}
          onPress={() => setCityModalVisible(true)}
        >
          <Text style={{ color: "white" }}>City</Text>
        </Pressable>
        <Pressable
          style={styles.filterBtn}
          onPress={() => setDoctorTypeModalVisible(true)}
        >
          <Text style={{ color: "white" }}>Doctor Type</Text>
        </Pressable>
      </View>

      <Pressable
        style={
          filteredCountry === "" &&
          filteredCity === "" &&
          filteredDoctorType === ""
            ? [styles.applyFilterBtn, { opacity: 0.5 }]
            : styles.applyFilterBtn
        }
        disabled={
          filteredCountry === "" &&
          filteredCity === "" &&
          filteredDoctorType === ""
        }
        onPress={handleApplyFilter}
      >
        <Text style={{ color: "white", marginRight: 3 }}>Apply Filter</Text>
        <Feather name="filter" size={24} color="white" />
      </Pressable>

      <Pressable style={styles.nearbyBtn} onPress={loadNearbyDoctors}>
        <Text style={{ marginRight: 3 }}>Doctors Nearby</Text>
        <Entypo name="location-pin" size={24} color="black" />
      </Pressable>

      <View
        style={{
          marginTop: 50,
          width: "90%",
          alignItems: "center",
          flex: 1,
        }}
      >
        <FlatList
          style={{ width: "100%" }}
          data={doctors}
          renderItem={({ item }) => (
            <View
              style={{
                backgroundColor: "#f4f5f2",
                width: "100%",
                marginBottom: 50,
                height: 200,
                borderRadius: 25,
                padding: 15,
                justifyContent: "space-around",
                shadowColor: "#000",
                shadowOffset: { width: 0, height: 4 },
                shadowOpacity: 0.3,
                shadowRadius: 4,
                elevation: 5,
              }}
            >
              <View style={{ marginLeft: 15 }}>
                <Text style={{ fontSize: 20, fontWeight: "bold" }}>
                  Dr. {item.user.fullName}
                </Text>
                <Text style={{ marginBottom: 15 }}>{item.doctorType}</Text>
                <Text>
                  <Text style={{ fontWeight: "bold" }}>Tel.:</Text>{" "}
                  {item.phoneNumber}
                </Text>
                <Text>
                  <Text style={{ fontWeight: "bold" }}>Email:</Text>{" "}
                  {item.user.email.includes("no.")
                    ? "Not available"
                    : item.user.email}
                </Text>
              </View>

              <View
                style={{
                  flexDirection: "row",
                  justifyContent: "center",
                  gap: 10,
                }}
              >
                <Pressable
                  style={({ pressed }) =>
                    pressed ? [styles.pressed, styles.viewBtn] : styles.viewBtn
                  }
                  onPress={() => {
                    navigation.navigate("Appointment", item);
                  }}
                >
                  <Text>View</Text>
                </Pressable>

                <Pressable
                  style={({ pressed }) =>
                    pressed ? [styles.pressed, styles.reqBtn] : styles.reqBtn
                  }
                  onPress={() => {
                    setSelectedDoctor(item);
                    setMessageModelOpen(true);
                  }}
                >
                  <Text style={{ color: "white" }}>Request Chat</Text>
                </Pressable>
              </View>
            </View>
          )}
          keyExtractor={(item) => item.id.toString()}
          keyboardShouldPersistTaps="always"
        />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    backgroundColor: "white",
  },

  applyFilterBtn: {
    marginTop: 20,
    backgroundColor: "black",
    borderRadius: 25,
    width: "80%",
    height: 30,
    justifyContent: "center",
    alignItems: "center",
    flexDirection: "row",
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
    justifyContent: "space-evenly",
    borderRadius: 15,
    padding: 10,
    width: "80%",
    height: "30%",
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },

  errorMessageModalView: {
    backgroundColor: "white",
    borderRadius: 15,
    padding: 10,
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },

  nearbyBtn: {
    marginTop: 20,
    backgroundColor: "white",
    borderRadius: 25,
    width: "80%",
    height: 30,
    justifyContent: "center",
    alignItems: "center",
    flexDirection: "row",
    borderWidth: 1,
  },

  filterBtn: {
    backgroundColor: "black",
    borderRadius: 25,
    width: "30%",
    height: 25,
    justifyContent: "center",
    alignItems: "center",
  },

  searchBtn: {
    backgroundColor: "#d0d3d9",
    width: "20%",
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 25,
    height: 40,
  },

  viewBtn: {
    width: "20%",
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 25,
    height: 36,
    borderWidth: 1,
  },

  reqBtn: {
    width: "32%",
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 25,
    height: 36,
    borderWidth: 1,
    backgroundColor: "black",
  },

  pressed: {
    opacity: 0.5,
  },

  suggestionsList: {
    marginTop: 8,
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 8,
    backgroundColor: "#fff",
    maxHeight: 150,
    width: "100%",
  },
  suggestionItem: {
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
  },
});
