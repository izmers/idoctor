-- Users
INSERT INTO USERX (CITY, COUNTRY, EMAIL, FULL_NAME, USERNAME, PASSWORD, ZIP, USER_IS_DOCTOR) VALUES
                                                                                                 ('Innsbruck', 'Austria', 'venuri.panditha-wijekoon-mudiyanselag@student.uibk.ac.at', 'Venuri Santhushi Wijekoon Panditha Wijekoon Mudiyanselag', 'venuri', '$2a$10$jDPX7a440kUgdHRl0X8Q8.hlPYJ74x0AYEhVkk2tsvkKcQE7OzzxG', 6020, false),
                                                                                                 ('Innsbruck', 'Austria', 'khrystyna.kokolius@student.uibk.ac.at', 'Khrystyna Kokolius', 'khrystyna', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', 6020, false),
                                                                                                 ('Innsbruck', 'Austria', 'marzhan.imanbazarova@student.uibk.ac.at', 'Marzhan Imanbazarova', 'marzhan', '$2a$10$EGI9MlcT9ci6B8muaJp2R.fHtxP.T/ApNmabgJucVj0uzM23mo9eO', 6020, false),
                                                                                                 ('Kufstein', 'Austria', 'remzi.cetin@student.uibk.ac.at', 'Remzi Cetin', 'remzi', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', 6330, false);


INSERT INTO USERX_ROLE (USERX_ID, ROLES) VALUES (1, 'USER'),
                                                (1, 'ADMIN'),
                                                (2, 'USER'),
                                                (2, 'ADMIN'),
                                                (3, 'USER'),
                                                (3, 'ADMIN');



-- Doctors
INSERT INTO USERX (CITY, COUNTRY, EMAIL, FULL_NAME, USERNAME, PASSWORD, ZIP, USER_IS_DOCTOR) VALUES
                                                                                                 ('Kufstein', 'Austria', 'info@drwaitz.at', 'Dietmar Waitz', 'dietmar', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', '6330', true),
                                                                                                 ('Kufstein', 'Austria', 'stefan.horak@gmail.com', 'Stefan Horak', 'stefan', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', '6330', true),
                                                                                                 ('Kufstein', 'Austria', 'ordination@internistin-fuchs.at', 'Julia Fuchs', 'julia', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', '6330', true),
                                                                                                 ('Kufstein', 'Austria', 'ordination@drkruger.at', 'Stephan Kruger', 'stephan', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', '6330', true),
                                                                                                 ('Kufstein', 'Austria', 'no.aneglika@email.com', 'Angelika Berek', 'angelika', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', '6330', true),
                                                                                                 ('Innsbruck', 'Austria', 'no.susanne@gmail.com', 'Susanne Dretnik', 'susanne', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', '6020', true),
                                                                                                 ('Innsbruck', 'Austria', 'max.mustermann@gmail.com', 'Max Mustermann', 'max', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', '6020', true),
                                                                                                 ('Innsbruck', 'Austria', 'john.doe@gmail.com', 'John Doe', 'john', '$2a$10$qg75DD.vmcnmj3o9M3cZk.5/bthdXq3Hdq83JyET5vfh.og06hFba', '6020', true);

INSERT INTO DOCTOR (USER_ID, DOCTOR_TYPE, PHONE_NUMBER, STREET, STATUS) VALUES
                                                                            (5, 'Dermatologist', '+43 5372 63668', 'Street 1', 'UNLOCKED'),
                                                                            (6, 'Dermatologist', '+43 5372 63009', 'Street 2', 'UNLOCKED'),
                                                                            (7, 'Internal Medicine and Nephrology', '+43 5372 222 10', 'Street 3', 'UNLOCKED'),
                                                                            (8, 'General Practitioner', '+43 5372 62445', 'Street 4', 'UNLOCKED'),
                                                                            (9, 'Anesthesiologist', '+43 5372 65128', 'Street 5', 'UNLOCKED'),
                                                                            (10, 'General Practitioner', '+43 512 586227', 'Street 6', 'UNLOCKED'),
                                                                            (11, 'Dermatologist', '+43 512 586227', 'Street 6', 'UNLOCKED'),
                                                                            (12, 'Dermatologist', '+43 512 586227', 'Street 6', 'UNLOCKED');

-- Slots
INSERT INTO SLOT (FREE_DAY, FREE_TIME, DOCTOR_ID) VALUES ('2024-12-16', '23:27', 1),
                                                         ('2024-12-16', '23:30', 1),
                                                         ('2024-12-10', '11:32', 1),
                                                         ('2024-12-11', '17:32', 1),
                                                         ('2026-12-30', '15:32', 1),
                                                         ('2026-12-30', '19:32', 1),

                                                         ('2026-12-07', '14:32', 2),
                                                         ('2026-12-07', '16:32', 2),
                                                         ('2026-12-08', '11:32', 2),
                                                         ('2026-12-08', '17:32', 2),
                                                         ('2026-12-08', '15:32', 2),
                                                         ('2026-12-08', '19:32', 2),

                                                         ('2026-12-07', '14:32', 3),
                                                         ('2026-12-07', '16:32', 3),
                                                         ('2026-12-08', '11:32', 3),
                                                         ('2026-12-08', '17:32', 3),
                                                         ('2026-12-08', '15:32', 3),
                                                         ('2026-12-08', '19:32', 3),

                                                         ('2026-12-07', '14:32', 4),
                                                         ('2026-12-07', '16:32', 4),
                                                         ('2026-12-08', '11:32', 4),
                                                         ('2026-12-08', '17:32', 4),
                                                         ('2026-12-08', '15:32', 4),
                                                         ('2026-12-08', '19:32', 4);


-- ChatChannel
-- INSERT INTO CHAT_CHANNEL(DOCTOR_ID, PATIENT_ID) VALUES (1, 4);