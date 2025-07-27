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

export default function FilterByCityModalComponent({
  modalVisible,
  handleCloseModal,
  setFilteredCity,
}: {
  modalVisible: boolean;
  handleCloseModal: () => void;
  setFilteredCity: (city: string) => void;
}) {
  const allCities: string[] = [
    "Kufstein",
    "Innsbruck",
    "New York",
    "Los Angeles",
    "Chicago",
    "Houston",
    "Phoenix",
    "Philadelphia",
    "San Antonio",
    "San Diego",
    "Dallas",
    "San Jose",
    "Austin",
    "Jacksonville",
    "Fort Worth",
    "Columbus",
    "Charlotte",
    "San Francisco",
    "Indianapolis",
    "Seattle",
    "Denver",
    "Washington, D.C.",
    "Boston",
    "El Paso",
    "Nashville",
    "Detroit",
    "Oklahoma City",
    "Las Vegas",
    "Memphis",
    "Louisville",
    "Baltimore",
    "Milwaukee",
    "Albuquerque",
    "Tucson",
    "Fresno",
    "Mesa",
    "Sacramento",
    "Atlanta",
    "Kansas City",
    "Colorado Springs",
    "Miami",
    "Raleigh",
    "Omaha",
    "Long Beach",
    "Virginia Beach",
    "Oakland",
    "Minneapolis",
    "Tulsa",
    "Arlington",
    "Tampa",
    "New Orleans",
    "Wichita",
  ];

  const [cities, setCities] = useState<string[]>(allCities);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [selectedCity, setSelectedCity] = useState<string>("");

  function filterCountry(value: string) {
    setSearchQuery(value);
    const filtered = allCities.filter((city) =>
      city.toLowerCase().includes(value.toLowerCase())
    );
    setCities(filtered);
  }

  function handleSelectCountry(country: string) {
    setSelectedCity((prev) => {
      const newSelection = prev === country ? "" : country;
      setFilteredCity(newSelection);
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
            data={cities}
            renderItem={({ item }) => (
              <Pressable
                style={styles.countryBtn}
                onPress={() => handleSelectCountry(item)}
              >
                <Text style={{ fontSize: 20 }}>{item}</Text>
                {selectedCity === item && (
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
