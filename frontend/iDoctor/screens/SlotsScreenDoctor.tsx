import { useCallback, useContext, useEffect, useState } from "react";
import {
  Button,
  FlatList,
  Keyboard,
  Modal,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import { calculateStartDate } from "../util/date-calculator";
import { AuthContext } from "../store/auth-context";
import {
  NavigationProp,
  useFocusEffect,
  useNavigation,
} from "@react-navigation/native";
import LoadingComponent from "../components/LoadingComponent";
import { FontAwesome6 } from "@expo/vector-icons";
import DatePicker from "react-native-modern-datepicker";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import Octicons from "@expo/vector-icons/Octicons";
import { DoctorSpecificDoctorStackParameterList } from "../App";
import { API_URL, request } from "../util/requests";

interface Slot {
  freeDay: string;
  freeTime: string;
}

interface User {
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

export default function SlotsScreenDoctor() {
  const [open, setOpen] = useState<boolean>(false);
  const [timeOpen, setTimeOpen] = useState<boolean>(false);
  const [date, setDate] = useState<string>(calculateStartDate);
  const [chosenSlots, setChosenSlots] = useState<Slot[]>([]);
  const [start, setStart] = useState<string>(calculateStartDate);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [slots, setSlots] = useState<Slot[]>([]);
  const [optionalText, setOptionalText] = useState<string>("");
  const [messageModalOpen, setMessageModelOpen] = useState<boolean>(false);
  const [currentDoctor, setCurrentDoctor] = useState<Doctor>();
  const authCtx = useContext(AuthContext);
  const navigation =
    useNavigation<NavigationProp<DoctorSpecificDoctorStackParameterList>>();

  useEffect(() => {
    async function loadCurrentUser() {
      await request<Doctor>({
        request: `${API_URL}/api/doctor/current`,
        token: authCtx.token,
        setIsLoading: setIsLoading,
        onSuccess: (resData: Doctor) => {
          setCurrentDoctor(resData);
        },
      });
    }

    loadCurrentUser();
  }, []);
  useFocusEffect(
    useCallback(() => {
      const startDate = calculateStartDate();
      setDate(startDate);
      setStart(startDate);
    }, [])
  );

  function handleChange(propDate: string) {
    setDate(propDate);
  }

  function handleTimeChange(propTime: string) {
    const newSlot = {
      freeDay: date,
      freeTime: propTime,
    };

    const slotExists = chosenSlots.some(
      (slot) =>
        slot.freeDay === newSlot.freeDay && slot.freeTime === newSlot.freeTime
    );

    if (!slotExists) {
      setChosenSlots((prevSlots) => [...prevSlots, newSlot]);
      const copyChosenSlots = [...chosenSlots, newSlot];
      const filteredSlots = copyChosenSlots.filter(
        (slot) => slot.freeDay === date
      );
      setSlots(filteredSlots);
    }

    setTimeOpen(false);
  }

  function handleOnPress() {
    const filteredSlots = chosenSlots.filter((slot) => slot.freeDay === date);
    setSlots(filteredSlots);
    setOpen(false);
  }

  function deleteSlot(freeDay: string, freeTime: string) {
    setChosenSlots((prevChosenSlots) =>
      prevChosenSlots.filter(
        (slot) => slot.freeDay !== freeDay || slot.freeTime !== freeTime
      )
    );

    setSlots((prevSlots) =>
      prevSlots.filter(
        (slot) => slot.freeDay !== freeDay || slot.freeTime !== freeTime
      )
    );
  }

  const formatTime = (time: string): string => {
    const [hour, minute] = time.split(":").map(Number);
    const period = hour >= 12 ? "PM" : "AM";
    const formattedHour = hour % 12 || 12;
    return `${formattedHour}:${minute.toString().padStart(2, "0")} ${period}`;
  };

  async function applyChosenSlots() {
    setIsLoading(true);
    try {
      const transformedSlots = chosenSlots.map((slot) => ({
        ...slot,
        freeDay: slot.freeDay.replace(/\//g, "-"),
      }));
      const response = await fetch(`${API_URL}/api/slot/bulk/create`, {
        method: "POST",
        body: JSON.stringify(transformedSlots),
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + authCtx.token,
        },
      });

      if (!response.ok) {
        throw new Error("error with response status code: " + response.status);
      }

      const resData = await response.json();
      setIsLoading(false);
    } catch (error) {
      if (error instanceof Error) {
        console.error(error.message);
      } else {
        console.error("An unknown error occurred", error);
      }
      setIsLoading(false);
      return;
    }

    setChosenSlots([]);
    setSlots([]);
  }

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <LoadingComponent />
      </View>
    );
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
              Dr. {currentDoctor?.user.fullName}
            </Text>
            <View
              style={{ flexDirection: "row", alignItems: "center", gap: 10 }}
            >
              <FontAwesome6 name="circle-check" size={20} color="green" />
              <Text style={{ color: "grey" }}>Verified</Text>
            </View>
          </View>
          <Text>{currentDoctor?.doctorType}</Text>
        </View>

        <View style={{ gap: 8 }}>
          <Text>
            Phone:{" "}
            <Text style={styles.link} onPress={() => {}}>
              {currentDoctor?.phoneNumber}
            </Text>
          </Text>
          <Text>
            Email:{" "}
            <Text style={styles.link} onPress={() => {}}>
              {currentDoctor?.user.email}
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

        <Modal animationType="slide" transparent={true} visible={timeOpen}>
          <View style={styles.centeredView}>
            <View style={styles.modalView}>
              <DatePicker mode="time" onTimeChange={handleTimeChange} />
            </View>
          </View>
        </Modal>

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
        <Button title="Choose Time Slots" onPress={() => setTimeOpen(true)} />

        <View style={styles.chosenSlotsFieldBtn}>
          <FlatList
            data={slots}
            keyExtractor={(item) => item.freeDay + item.freeTime}
            renderItem={({ item }) => (
              <View style={[styles.slotBtn, { position: "relative" }]}>
                <Octicons
                  name="x-circle-fill"
                  size={18}
                  color="white"
                  style={{ position: "absolute", top: -0.5, right: -0.5 }}
                  onPress={() => deleteSlot(item.freeDay, item.freeTime)}
                />
                <Text style={{ color: "white" }}>
                  {formatTime(item.freeTime)}
                </Text>
              </View>
            )}
            numColumns={3}
            columnWrapperStyle={{ marginBottom: 10 }}
            keyboardShouldPersistTaps="handled"
          />
        </View>
      </View>

      <Pressable
        style={({ pressed }) =>
          pressed
            ? [styles.pressed, styles.bookAppBtn]
            : chosenSlots.length === 0
            ? [{ opacity: 0.6 }, styles.bookAppBtn]
            : styles.bookAppBtn
        }
        disabled={chosenSlots.length === 0}
        onPress={applyChosenSlots}
      >
        <Text style={{ fontWeight: "bold", color: "white", fontSize: 15 }}>
          Apply
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

  chosenSlotsFieldBtn: {
    alignItems: "center",
    height: "80%",
    marginTop: 10,
    backgroundColor: "#f4f5f2",
    borderRadius: 25,
    paddingTop: 10,
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
