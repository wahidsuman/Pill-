import React, { useState, useEffect } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, TouchableOpacity, ScrollView, SafeAreaView, TextInput, Platform } from 'react-native';
import DateTimePicker from '@react-native-community/datetimepicker';

export default function App() {
  const [currentTime, setCurrentTime] = useState(new Date());
  const [showAddMedication, setShowAddMedication] = useState(false);
  const [selectedColor, setSelectedColor] = useState('#2196F3');
  const [selectedFrequency, setSelectedFrequency] = useState('Daily');
  const [medicationName, setMedicationName] = useState('');
  const [reminderTime, setReminderTime] = useState(new Date());
  const [showTimePicker, setShowTimePicker] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const formatDate = (date: Date) => {
    const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    
    const dayName = days[date.getDay()];
    const monthName = months[date.getMonth()];
    const day = date.getDate();
    const year = date.getFullYear();
    
    let hours = date.getHours();
    const minutes = date.getMinutes();
    const ampm = hours >= 12 ? 'pm' : 'am';
    hours = hours % 12;
    hours = hours ? hours : 12;
    const minutesStr = minutes < 10 ? '0' + minutes : minutes;
    
    return `${dayName}, ${monthName} ${String(day).padStart(2, '0')}, ${year} · ${String(hours).padStart(2, '0')}:${minutesStr} ${ampm}`;
  };

  const formatTime = (date: Date) => {
    let hours = date.getHours();
    const minutes = date.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12;
    const minutesStr = minutes < 10 ? '0' + minutes : minutes;
    return `${String(hours).padStart(2, '0')}:${minutesStr} ${ampm}`;
  };

  const onTimeChange = (event: any, selectedDate?: Date) => {
    setShowTimePicker(Platform.OS === 'ios');
    if (selectedDate) {
      setReminderTime(selectedDate);
    }
  };

  const colors = [
    { color: '#2196F3', name: 'Blue' },
    { color: '#F44336', name: 'Red' },
    { color: '#4CAF50', name: 'Green' },
    { color: '#FF9800', name: 'Orange' },
    { color: '#9C27B0', name: 'Purple' },
  ];

  if (showAddMedication) {
    // Add Medication Screen
    return (
      <SafeAreaView style={styles.container}>
        <StatusBar style="dark" />
        <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
          {/* Header */}
          <View style={styles.addMedHeader}>
            <Text style={styles.addMedTitle}>Add New Medication</Text>
          </View>

          {/* Medication Name Input */}
          <View style={styles.formSection}>
            <TextInput
              style={styles.input}
              placeholder="Medication Name"
              placeholderTextColor="#999"
              value={medicationName}
              onChangeText={setMedicationName}
            />
          </View>

          {/* Pill Representation Section */}
          <View style={styles.formSection}>
            <Text style={styles.formLabel}>Pill Representation</Text>
            <View style={styles.representationButtons}>
              <TouchableOpacity style={styles.representationButton}>
                <Text style={styles.representationIcon}>📷</Text>
                <Text style={styles.representationText}>Take Photo</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.representationButton}>
                <Text style={styles.representationIcon}>🎨</Text>
                <Text style={styles.representationText}>Use Color</Text>
              </TouchableOpacity>
            </View>
            
            {/* Color Picker */}
            <View style={styles.colorPicker}>
              {colors.map((item) => (
                <TouchableOpacity
                  key={item.color}
                  style={[styles.colorButton, { backgroundColor: item.color }]}
                  onPress={() => setSelectedColor(item.color)}
                >
                  {selectedColor === item.color && (
                    <Text style={styles.checkmark}>✓</Text>
                  )}
                </TouchableOpacity>
              ))}
            </View>
          </View>

          {/* Frequency Selection */}
          <View style={styles.formSection}>
            <Text style={styles.formLabel}>Frequency</Text>
            <View style={styles.frequencyButtons}>
              {['Daily', 'Weekly', 'Monthly', 'Custom'].map((freq) => (
                <TouchableOpacity
                  key={freq}
                  style={[
                    styles.frequencyButton,
                    selectedFrequency === freq && styles.frequencyButtonActive
                  ]}
                  onPress={() => setSelectedFrequency(freq)}
                >
                  <Text style={[
                    styles.frequencyText,
                    selectedFrequency === freq && styles.frequencyTextActive
                  ]}>
                    {freq}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>

          {/* Reminder Times */}
          <View style={styles.formSection}>
            <Text style={styles.formLabel}>Reminder Times</Text>
            <TouchableOpacity 
              style={styles.timePickerContainer}
              onPress={() => setShowTimePicker(true)}
            >
              <Text style={styles.timePickerIcon}>🕐</Text>
              <Text style={styles.timePickerText}>{formatTime(reminderTime)}</Text>
            </TouchableOpacity>
            {showTimePicker && (
              <DateTimePicker
                value={reminderTime}
                mode="time"
                is24Hour={false}
                display="default"
                onChange={onTimeChange}
              />
            )}
            <TouchableOpacity style={styles.addTimeButton}>
              <Text style={styles.addTimeText}>+ Add another time</Text>
            </TouchableOpacity>
          </View>

          <View style={{ height: 120 }} />
        </ScrollView>

        {/* Bottom Action Buttons */}
        <View style={styles.bottomActions}>
          <TouchableOpacity 
            style={styles.cancelButton}
            onPress={() => setShowAddMedication(false)}
          >
            <Text style={styles.cancelButtonText}>Cancel</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.addPillButton}>
            <Text style={styles.addPillButtonText}>Add Pill</Text>
          </TouchableOpacity>
        </View>

        {/* Bottom Navigation Bar */}
        <View style={styles.bottomNav}>
          <TouchableOpacity style={styles.navItem}>
            <Text style={styles.navIconActive}>🏠</Text>
            <Text style={styles.navLabelActive}>Home</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.navItem}>
            <Text style={styles.navIcon}>📊</Text>
            <Text style={styles.navLabel}>Stats</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.navItem}>
            <Text style={styles.navIcon}>📅</Text>
            <Text style={styles.navLabel}>Calendar</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.navItem}>
            <Text style={styles.navIcon}>⚙️</Text>
            <Text style={styles.navLabel}>Settings</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  // Home Screen
  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />
      <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
        {/* Top Section - Title Card */}
        <View style={styles.headerCard}>
          <Text style={styles.headerTitle}>Pill Reminder</Text>
          <Text style={styles.headerSubtitle}>Never forget your Medicines</Text>
          <Text style={styles.dateTime}>{formatDate(currentTime)}</Text>
        </View>

        {/* Statistics Section */}
        <View style={styles.statsContainer}>
          <View style={styles.statCard}>
            <Text style={styles.statNumber}>0</Text>
            <Text style={styles.statLabel}>Taken Today</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={styles.statNumber}>0</Text>
            <Text style={styles.statLabel}>Pending</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={styles.statNumber}>0</Text>
            <Text style={styles.statLabel}>Total</Text>
          </View>
        </View>

        {/* Next Reminders Section */}
        <View style={styles.sectionCard}>
          <View style={styles.sectionHeader}>
            <Text style={styles.iconBell}>🔔</Text>
            <Text style={styles.sectionTitle}>Next Reminders</Text>
          </View>
          <Text style={styles.emptyText}>No upcoming reminders</Text>
        </View>

        {/* My Medication Section */}
        <View style={styles.sectionCard}>
          <View style={styles.sectionHeader}>
            <Text style={styles.iconBottle}>💊</Text>
            <Text style={styles.sectionTitle}>My Medication</Text>
          </View>
          <Text style={styles.emptyText}>No medications added yet</Text>
          <Text style={styles.hintText}>Add your first medication using the + button above</Text>
        </View>

        <View style={{ height: 100 }} />
      </ScrollView>

      {/* Floating Add Button */}
      <TouchableOpacity 
        style={styles.floatingButton}
        onPress={() => setShowAddMedication(true)}
      >
        <Text style={styles.floatingButtonText}>+</Text>
      </TouchableOpacity>

      {/* Bottom Navigation Bar */}
      <View style={styles.bottomNav}>
        <TouchableOpacity style={styles.navItem}>
          <Text style={styles.navIconActive}>🏠</Text>
          <Text style={styles.navLabelActive}>Home</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.navItem}>
          <Text style={styles.navIcon}>📊</Text>
          <Text style={styles.navLabel}>Stats</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.navItem}>
          <Text style={styles.navIcon}>📅</Text>
          <Text style={styles.navLabel}>Calendar</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.navItem}>
          <Text style={styles.navIcon}>⚙️</Text>
          <Text style={styles.navLabel}>Settings</Text>
          </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F7FA',
  },
  scrollView: {
    flex: 1,
  },
  headerCard: {
    backgroundColor: '#2196F3',
    padding: 20,
    marginTop: 40,
    marginHorizontal: 16,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  headerTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  headerSubtitle: {
    fontSize: 16,
    color: '#E3F2FD',
    marginTop: 4,
  },
  dateTime: {
    fontSize: 14,
    color: '#E3F2FD',
    marginTop: 8,
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginHorizontal: 16,
    marginTop: 16,
  },
  statCard: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    padding: 16,
    marginHorizontal: 4,
    borderRadius: 12,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  statNumber: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#2196F3',
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
    marginTop: 4,
    textAlign: 'center',
  },
  sectionCard: {
    backgroundColor: '#FFFFFF',
    padding: 20,
    marginHorizontal: 16,
    marginTop: 16,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  iconBell: {
    fontSize: 24,
    marginRight: 8,
  },
  iconBottle: {
    fontSize: 24,
    marginRight: 8,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
  },
  emptyText: {
    fontSize: 14,
    color: '#999',
    textAlign: 'center',
    marginVertical: 8,
  },
  hintText: {
    fontSize: 12,
    color: '#BBB',
    textAlign: 'center',
    marginTop: 4,
  },
  floatingButton: {
    position: 'absolute',
    right: 20,
    bottom: 90,
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: '#2196F3',
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevation: 8,
  },
  floatingButtonText: {
    fontSize: 32,
    color: '#FFFFFF',
    fontWeight: '300',
  },
  bottomNav: {
    flexDirection: 'row',
    backgroundColor: '#FFFFFF',
    paddingVertical: 8,
    paddingBottom: 12,
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: -2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 8,
  },
  navItem: {
    flex: 1,
    alignItems: 'center',
    paddingVertical: 8,
  },
  navIcon: {
    fontSize: 24,
    opacity: 0.5,
  },
  navIconActive: {
    fontSize: 24,
  },
  navLabel: {
    fontSize: 11,
    color: '#999',
    marginTop: 4,
  },
  navLabelActive: {
    fontSize: 11,
    color: '#2196F3',
    marginTop: 4,
    fontWeight: '600',
  },
  // Add Medication Screen Styles
  addMedHeader: {
    paddingHorizontal: 20,
    paddingTop: 40,
    paddingBottom: 10,
  },
  addMedTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#333',
  },
  formSection: {
    paddingHorizontal: 20,
    marginTop: 24,
  },
  formLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 12,
  },
  input: {
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 16,
    color: '#333',
  },
  representationButtons: {
    flexDirection: 'row',
    gap: 12,
  },
  representationButton: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    paddingVertical: 16,
    paddingHorizontal: 12,
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 8,
  },
  representationIcon: {
    fontSize: 20,
  },
  representationText: {
    fontSize: 14,
    color: '#333',
    fontWeight: '500',
  },
  colorPicker: {
    flexDirection: 'row',
    gap: 12,
    marginTop: 16,
    justifyContent: 'center',
  },
  colorButton: {
    width: 50,
    height: 50,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
    elevation: 3,
  },
  checkmark: {
    color: '#FFFFFF',
    fontSize: 24,
    fontWeight: 'bold',
  },
  frequencyButtons: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  frequencyButton: {
    minWidth: '22%',
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 20,
    paddingVertical: 12,
    paddingHorizontal: 8,
    alignItems: 'center',
  },
  frequencyButtonActive: {
    backgroundColor: '#E3F2FD',
    borderColor: '#2196F3',
  },
  frequencyText: {
    fontSize: 14,
    color: '#666',
    fontWeight: '500',
  },
  frequencyTextActive: {
    color: '#2196F3',
    fontWeight: '600',
  },
  timePickerContainer: {
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 14,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  timePickerIcon: {
    fontSize: 20,
  },
  timePickerText: {
    fontSize: 16,
    color: '#333',
    fontWeight: '500',
  },
  addTimeButton: {
    marginTop: 12,
  },
  addTimeText: {
    fontSize: 14,
    color: '#00BCD4',
    fontWeight: '500',
  },
  bottomActions: {
    flexDirection: 'row',
    paddingHorizontal: 20,
    paddingVertical: 16,
    gap: 12,
    backgroundColor: '#FFFFFF',
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
  },
  cancelButton: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
  },
  cancelButtonText: {
    fontSize: 16,
    color: '#666',
    fontWeight: '600',
  },
  addPillButton: {
    flex: 1,
    backgroundColor: '#9E9E9E',
    borderRadius: 8,
    paddingVertical: 14,
    alignItems: 'center',
  },
  addPillButtonText: {
    fontSize: 16,
    color: '#FFFFFF',
    fontWeight: '600',
  },
});
