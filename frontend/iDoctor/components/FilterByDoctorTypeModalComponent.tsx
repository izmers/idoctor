import { AntDesign } from "@expo/vector-icons";
import { useState } from "react";
import {
  Button,
  FlatList,
  Modal,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

export default function FilterByDoctorTypeModalComponent({
  modalVisible,
  handleCloseModal,
  setFilteredDoctorType,
}: {
  modalVisible: boolean;
  handleCloseModal: () => void;
  setFilteredDoctorType: (doctorType: string) => void;
}) {
  const allDoctorTypes: string[] = [
    "Internal Medicine and Nephrology",
    "Allergist/Immunologist",
    "Anesthesiologist",
    "Cardiologist",
    "Dermatologist",
    "Endocrinologist",
    "Gastroenterologist",
    "General Practitioner",
    "Geriatrician",
    "Hematologist",
    "Infectious Disease Specialist",
    "Internal Medicine Specialist",
    "Nephrologist",
    "Neurologist",
    "Obstetrician/Gynecologist (OB/GYN)",
    "Oncologist",
    "Ophthalmologist",
    "Orthopedic Surgeon",
    "Otolaryngologist (ENT)",
    "Pathologist",
    "Pediatrician",
    "Psychiatrist",
    "Pulmonologist",
    "Radiologist",
    "Rheumatologist",
    "Surgeon",
    "Urologist",
    "Emergency Medicine Specialist",
    "Sports Medicine Specialist",
    "Plastic Surgeon",
    "Podiatrist",
    "Dentist",
    "Chiropractor",
    "Occupational Medicine Specialist",
    "Physical Medicine and Rehabilitation Specialist",
    "Pain Management Specialist",
    "Sleep Medicine Specialist",
    "Addiction Medicine Specialist",
    "Geneticist",
    "Neuropsychiatrist",
  ];

  const [doctorTypes, setDoctorTypes] = useState<string[]>(allDoctorTypes);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [selectedDoctorType, setSelectedDoctorType] = useState<string>("");

  function filterCountry(value: string) {
    setSearchQuery(value);
    const filtered = allDoctorTypes.filter((doctorType) =>
      doctorType.toLowerCase().includes(value.toLowerCase())
    );
    setDoctorTypes(filtered);
  }

  function handleSelectCountry(country: string) {
    setSelectedDoctorType((prev) => {
      const newSelection = prev === country ? "" : country;
      setFilteredDoctorType(newSelection);
      return newSelection;
    });
  }
  return (
    <Modal animationType="slide" transparent={true} visible={modalVisible}>
      <View style={styles.modalContainer}>
        <View style={styles.modalView}>
          <TextInput
            style={{
              width: "90%",
              height: 45,
              padding: 10,
              borderWidth: 1,
              borderRadius: 25,
              marginTop: 15,
            }}
            placeholder="Search for country"
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onChangeText={filterCountry}
            value={searchQuery}
          />

          <FlatList
            style={{ width: "90%", marginTop: 10 }}
            data={doctorTypes}
            renderItem={({ item }) => (
              <Pressable
                style={styles.countryBtn}
                onPress={() => handleSelectCountry(item)}
              >
                <Text style={{ fontSize: 20 }}>{item}</Text>
                {selectedDoctorType === item && (
                  <AntDesign name="checkcircle" size={24} color="black" />
                )}
              </Pressable>
            )}
            keyExtractor={(item) => item}
          />
          <Button title="Close" onPress={handleCloseModal} />
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },

  modalContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(230, 237, 232, 0.5)",
  },

  modalView: {
    backgroundColor: "white",
    alignItems: "center",
    borderRadius: 15,
    padding: 10,
    width: "90%",
    height: "85%",
  },

  countryBtn: {
    borderBottomWidth: 0.2,
    height: 50,
    alignItems: "center",
    justifyContent: "space-between",
    flexDirection: "row",
  },
});
