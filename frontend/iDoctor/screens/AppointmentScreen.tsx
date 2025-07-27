import {
  NavigationProp,
  RouteProp,
  useFocusEffect,
  useNavigation,
  useRoute,
} from "@react-navigation/native";
import {
  Button,
  FlatList,
  Keyboard,
  Linking,
  Modal,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import { DoctorsStackParameterList } from "../App";
import FontAwesome6 from "@expo/vector-icons/FontAwesome6";
import { useCallback, useContext, useState } from "react";
import DatePicker from "react-native-modern-datepicker";
import { calculateStartDate } from "../util/date-calculator";
import { AuthContext } from "../store/auth-context";
import FontAwesome from "@expo/vector-icons/FontAwesome";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import LoadingComponent from "../components/LoadingComponent";
import { API_URL, request } from "../util/requests";

type AppoinmentBookingRouteProp = RouteProp<
  DoctorsStackParameterList,
  "Appointment"
>;

interface Slot {
  id: number;
  freeDay: string;
  freeTime: string;
}

export default function AppointmentScreen() {
  const route = useRoute<AppoinmentBookingRouteProp>();
  const [open, setOpen] = useState<boolean>(false);
  const [date, setDate] = useState<string>(calculateStartDate);
  const [start, setStart] = useState<string>(calculateStartDate);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [slots, setSlots] = useState<Slot[]>([]);
  const [optionalText, setOptionalText] = useState<string>("");
  const [messageModalOpen, setMessageModelOpen] = useState<boolean>(false);
  const [selectedSlot, setSelectedSlot] = useState<Slot | undefined>();
  const authCtx = useContext(AuthContext);
  const navigation = useNavigation<NavigationProp<DoctorsStackParameterList>>();

  useFocusEffect(
    useCallback(() => {
      const startDate = calculateStartDate();
      setDate(startDate);
      setStart(startDate);
    }, [])
  );

  const handlePhonePress = async (phoneNumber: string) => {
    const cleanedNumber = phoneNumber.replace(/\s+/g, "");
    const url = `tel:${cleanedNumber}`;

    const supported = await Linking.canOpenURL(url);
    if (supported) {
      Linking.openURL(url).catch((err) =>
        console.error("Failed to open phone link:", err)
      );
    } else {
      console.error("Phone calling is not supported on this device.");
    }
  };

  const handleEmailPress = (email: string) => {
    Linking.openURL(`mailto:${email}`).catch((err) =>
      console.error("Failed to open email link:", err)
    );
  };

  async function handleOnPress() {
    setOpen(false);
    const formattedDate = date.replace(/\//g, "-");
    await request<Slot[]>({
      request: `${API_URL}/api/slot/available/by-date/${route.params.id}/${formattedDate}`,
      token: authCtx.token,
      setIsLoading: setIsLoading,
      onSuccess: (resData: Slot[]) => {
        const transformedData: Slot[] = resData.map((slot: Slot) => {
          const [hours, minutes] = slot.freeTime.split(":");
          const date = new Date();
          date.setHours(Number(hours), Number(minutes));

          const formattedTime = date.toLocaleTimeString("en-US", {
            hour: "numeric",
            minute: "numeric",
          });

          return {
            ...slot,
            freeTime: formattedTime,
          };
        });

        setSlots(transformedData);
      },
    });
  }

  async function handleBookAppointment() {
    await request({
      method: "POST",
      strBody: JSON.stringify({ additionalNote: optionalText }),
      request: `${API_URL}/api/appointment/book/${route.params.id}/${
        selectedSlot!.id
      }`,
      token: authCtx.token,
      setIsLoading: setIsLoading,
      onSuccess: (_) => navigation.goBack(),
    });
  }

  function handleChange(propDate: string) {
    setDate(propDate);
  }

  if (isLoading) {
    return <LoadingComponent />;
  }

  return (
    <View style={styles.container}>
      <View
        style={{
          backgroundColor: "#f4f5f2",
          width: "90%",
          padding: 20,
          height: "23%",
          justifyContent: "space-between",
          borderRadius: 25,
          marginTop: 20,
          shadowColor: "#000",
          shadowOffset: { width: 0, height: 4 },
          shadowOpacity: 0.3,
          shadowRadius: 4,
          elevation: 5,
        }}
      >
        <View style={{ gap: 8 }}>
          <View
            style={{
              flexDirection: "row",
              alignItems: "center",
              justifyContent: "space-between",
            }}
          >
            <Text style={{ fontWeight: "bold", fontSize: 20 }}>
              Dr. {route.params.user.fullName}
            </Text>
            <View
              style={{ flexDirection: "row", alignItems: "center", gap: 10 }}
            >
              <FontAwesome6 name="circle-check" size={20} color="green" />
              <Text style={{ color: "grey" }}>Verified</Text>
            </View>
          </View>
          <Text>{route.params.doctorType}</Text>
        </View>

        <View style={{ gap: 8 }}>
          <Text>
            Phone:{" "}
            <Text
              style={styles.link}
              onPress={() => handlePhonePress(route.params.phoneNumber)}
            >
              {route.params.phoneNumber}
            </Text>
          </Text>
          <Text>
            Email:{" "}
            <Text
              style={styles.link}
              onPress={() => handleEmailPress(route.params.user.email)}
            >
              {route.params.user.email}
            </Text>
          </Text>
        </View>
      </View>

      <View style={{ marginTop: 20, width: "90%" }}>
        <Text style={{ fontSize: 18 }}>Select Date</Text>
        <TouchableOpacity onPress={() => setOpen(true)}>
          <View
            style={{
              backgroundColor: "#f4f5f2",
              borderRadius: 25,
              height: 50,
              justifyContent: "center",
              paddingLeft: 10,
              marginTop: 10,
              borderWidth: 1,
              borderColor: "grey",
            }}
          >
            <Text style={{ color: "grey" }}>{date}</Text>
          </View>
        </TouchableOpacity>

        <Modal animationType="slide" transparent={true} visible={open}>
          <View style={styles.centeredView}>
            <View style={styles.modalView}>
              <DatePicker
                mode="calendar"
                minimumDate={start}
                selected={date}
                onDateChange={handleChange}
              />

              <TouchableOpacity onPress={handleOnPress}>
                <Text>Select</Text>
              </TouchableOpacity>
            </View>
          </View>
        </Modal>
      </View>

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
                placeholder="Add a note or a reason for your appointment"
                placeholderTextColor="grey"
                returnKeyType="default"
                autoCorrect={false}
                autoComplete="off"
                autoCapitalize="none"
              />
              <Button title="Done" onPress={() => setMessageModelOpen(false)} />
            </View>
          </KeyboardAwareScrollView>
        </TouchableWithoutFeedback>
      </Modal>

      <View
        style={{
          width: "90%",
          height: "30%",
          marginTop: 20,
        }}
      >
        <Text style={{ fontSize: 18 }}>Available Time Slots</Text>

        <View
          style={{
            alignItems: "center",
            height: "80%",
            marginTop: 10,
            backgroundColor: "#f4f5f2",
            borderRadius: 25,
            paddingTop: 10,
          }}
        >
          <FlatList
            data={slots}
            keyExtractor={(item) => item.id.toString()}
            renderItem={({ item }) => (
              <Pressable
                style={({ pressed }) =>
                  pressed
                    ? [styles.pressed, styles.slotBtn]
                    : selectedSlot !== undefined && selectedSlot.id === item.id
                    ? [{ opacity: 0.6 }, styles.slotBtn]
                    : styles.slotBtn
                }
                onPress={() => {
                  setSelectedSlot((prev) => {
                    if (prev !== undefined && prev.id === item.id) {
                      return undefined;
                    }
                    return item;
                  });
                }}
              >
                <Text style={{ color: "white" }}>{item.freeTime}</Text>
              </Pressable>
            )}
            numColumns={3}
            columnWrapperStyle={{ marginBottom: 10 }}
            keyboardShouldPersistTaps="handled"
          />
        </View>
      </View>

      <View
        style={{
          width: "90%",
          marginTop: 10,
          height: "10%",
        }}
      >
        <Text style={{ fontSize: 18 }}>Additional Information (Optional)</Text>
        <View
          style={{
            width: "100%",
            alignItems: "center",
          }}
        >
          <Pressable
            style={({ pressed }) =>
              pressed ? [styles.pressed, styles.messageBtn] : styles.messageBtn
            }
            onPress={() => setMessageModelOpen(true)}
          >
            {!optionalText || optionalText.trim().length === 0 ? (
              <FontAwesome name="envelope-open" size={24} color="black" />
            ) : (
              <FontAwesome name="envelope" size={24} color="black" />
            )}
          </Pressable>
        </View>
      </View>

      <Pressable
        style={({ pressed }) =>
          pressed
            ? [styles.pressed, styles.bookAppBtn]
            : !selectedSlot
            ? [{ opacity: 0.6 }, styles.bookAppBtn]
            : styles.bookAppBtn
        }
        disabled={!selectedSlot}
        onPress={handleBookAppointment}
      >
        <Text style={{ fontWeight: "bold", color: "white", fontSize: 15 }}>
          Book Appointment
        </Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    backgroundColor: "white",
    justifyContent: "space-evenly",
  },
  link: {
    color: "blue",
    textDecorationLine: "underline",
  },

  centeredView: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    marginTop: 22,
  },

  modalView: {
    margin: 20,
    backgroundColor: "white",
    borderRadius: 20,
    width: "90%",
    padding: 35,
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
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

  bookAppBtn: {
    width: "90%",
    backgroundColor: "black",
    borderRadius: 25,
    justifyContent: "center",
    alignItems: "center",
    height: "6%",
  },

  messageBtn: {
    alignItems: "center",
    borderRadius: 25,
    borderWidth: 1,
    justifyContent: "center",
    height: "65%",
    marginTop: 10,
    width: "100%",
  },

  slotBtn: {
    backgroundColor: "black",
    width: 100,
    height: 35,
    marginHorizontal: 15,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 25,
  },

  pressed: {
    opacity: 0.5,
  },
});
