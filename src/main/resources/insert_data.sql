// test data

// the admin cardentials : username: hafsa@gmail.com , password: admin12345


//other users :
INSERT INTO USERS (
    ID,
    NOM,
    PRENOM,
    EMAIL,
    PASSWORD,
    TELEPHONE,
    ROLE,
    ACTIF,
    DATE_INSCRIPTION
) VALUES
(8, 'Dubois', 'Thomas', 'thomas.dubois@test.com', 'password123', '0611223344', 'CLIENT', TRUE, '2025-01-10 09:00:00'),
(9, 'Lefevre', 'Marie', 'marie.lefevre@test.com', 'password123', '0622334455', 'ORGANIZER', TRUE, '2025-01-12 14:30:00'),
(10, 'Moreau', 'Lucas', 'lucas.moreau@test.com', 'password123', '0633445566', 'CLIENT', TRUE, '2025-01-15 11:15:00'),
(11, 'Petit', 'Camille', 'camille.petit@test.com', 'password123', '0644556677', 'ORGANIZER', TRUE, '2025-01-20 08:45:00'),
(12, 'Roux', 'Nicolas', 'nicolas.roux@test.com', 'password123', '0655667788', 'CLIENT', TRUE, '2025-02-01 10:20:00'),
(13, 'Fournier', 'Sarah', 'sarah.fournier@test.com', 'password123', '0666778899', 'ORGANIZER', TRUE, '2025-02-05 16:50:00'),
(14, 'Girard', 'Paul', 'paul.girard@test.com', 'password123', '0677889900', 'CLIENT', FALSE, '2025-02-10 13:00:00'),
(15, 'Bonnet', 'Emma', 'emma.bonnet@test.com', 'password123', '0688990011', 'CLIENT', TRUE, '2025-02-15 09:30:00'),
(16, 'Dupuis', 'Julien', 'julien.dupuis@test.com', 'password123', '0699001122', 'ORGANIZER', TRUE, '2025-02-20 15:10:00'),
(17, 'Lambert', 'Alice', 'alice.lambert@test.com', 'password123', '0600112233', 'ORGANIZER', TRUE, '2025-03-01 11:00:00');

//events :
INSERT INTO EVENTS (
    ID,
    TITRE,
    DESCRIPTION,
    CATEGORIE,
    DATE_DEBUT,
    DATE_FIN,
    LIEU,
    VILLE,
    CAPACITE_MAX,
    PRIX_UNITAIRE,
    STATUT,
    IMAGE_URL,
    ORGANISATEUR_ID,
    DATE_CREATION,
    DATE_MODIFICATION
) VALUES
-- 1. Tech & Business (Jan - Mar 2026) -> CONFERENCE
(151, 'Casablanca AI Summit 2026', 'Le futur de l''IA en Afrique du Nord.', 'CONFERENCE', '2026-01-15 09:00:00', '2026-01-17 18:00:00', 'Hyatt Regency', 'Casablanca', 600, 350.00, 'PUBLIE', 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),
(152, 'Morocco E-Com Days', 'Tendances du e-commerce pour 2026.', 'CONFERENCE', '2026-02-10 10:00:00', '2026-02-11 17:00:00', 'Technopark', 'Casablanca', 100, 200.00, 'PUBLIE', 'https://images.unsplash.com/photo-1556761175-5973dc0f32e7?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(153, 'Green Tech Forum', 'Technologies pour un avenir durable.', 'CONFERENCE', '2026-03-05 09:00:00', '2026-03-06 18:00:00', 'Palais des Congrès', 'Marrakech', 400, 500.00, 'PUBLIE', 'https://images.unsplash.com/photo-1518105779142-d975f22f1b0a?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(154, 'Cyber Defense 2026', 'Séminaire sur la cybersécurité bancaire.', 'CONFERENCE', '2026-02-20 14:00:00', '2026-02-20 18:00:00', 'Sofitel Jardin des Roses', 'Rabat', 50, 1500.00, 'BROUILLON', 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(155, 'Startup Africa Tour', 'Rencontre investisseurs et startups.', 'CONFERENCE', '2026-03-25 18:00:00', '2026-03-25 22:00:00', 'Hotel Marriott', 'Tanger', 150, 100.00, 'PUBLIE', 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),

-- 2. Culture & Festivals (Spring 2026) -> CONCERT / THEATRE
(156, 'Mawazine Festival Preview', 'Concert d''ouverture exclusif.', 'CONCERT', '2026-05-15 19:00:00', '2026-05-15 23:30:00', 'OLM Souissi', 'Rabat', 10000, 200.00, 'PUBLIE', 'https://images.unsplash.com/photo-1533174072545-e8d4aa97edf9?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(157, 'Marrakech Comedy Club', 'Nouveaux talents de l''humour.', 'THEATRE', '2026-04-10 20:00:00', '2026-04-10 23:00:00', 'Théâtre Royal', 'Marrakech', 800, 150.00, 'PUBLIE', 'https://images.unsplash.com/photo-1585699324551-f6c309eedeca?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(158, 'Classical Music Night', 'Orchestre Philharmonique du Maroc.', 'CONCERT', '2026-02-14 19:30:00', '2026-02-14 22:00:00', 'Théâtre Mohammed V', 'Rabat', 1200, 300.00, 'PUBLIE', 'https://images.unsplash.com/photo-1507838153414-b4b71359358c?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(159, 'Desert Blues Festival', 'Musique traditionnelle saharienne.', 'CONCERT', '2026-04-05 16:00:00', '2026-04-07 02:00:00', 'Mhamid El Ghizlane', 'Zagora', 500, 100.00, 'PUBLIE', 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(160, 'Fes Sacred Music 2026', 'Édition spéciale spiritualité.', 'CONCERT', '2026-06-01 18:00:00', '2026-06-08 23:00:00', 'Bab Makina', 'Fes', 2000, 450.00, 'BROUILLON', 'https://images.unsplash.com/photo-1514525253440-b393452e8d26?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),

-- 3. Sports & Adventure (2026) -> SPORT
(161, 'Ironman 70.3 Tangier', 'Triathlon international.', 'SPORT', '2026-10-04 07:00:00', '2026-10-04 16:00:00', 'Corniche', 'Tanger', 1500, 2000.00, 'PUBLIE', 'https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(162, 'Casablanca Run 2026', 'Course caritative 10km.', 'SPORT', '2026-03-08 09:00:00', '2026-03-08 12:00:00', 'Parc de la Ligue Arabe', 'Casablanca', 3000, 50.00, 'PUBLIE', 'https://images.unsplash.com/photo-1552674605-5d226874871d?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(163, 'Atlas Mountain Race', 'Course cycliste ultra-distance.', 'SPORT', '2026-02-15 08:00:00', '2026-02-20 18:00:00', 'Marrakech to Agadir', 'Marrakech', 200, 500.00, 'PUBLIE', 'https://images.unsplash.com/photo-1541625602330-2277a4c46182?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(164, 'Taghazout Surf Pro', 'Qualification mondiale surf.', 'SPORT', '2026-01-20 08:00:00', '2026-01-25 17:00:00', 'Anchor Point', 'Taghazout', 1000, 0.00, 'PUBLIE', 'https://images.unsplash.com/photo-1502680390469-be75c86b636f?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(165, 'Sahara Trek 2026', 'Marche solidaire dans le désert.', 'SPORT', '2026-11-10 07:00:00', '2026-11-15 18:00:00', 'Merzouga Dunes', 'Merzouga', 50, 3000.00, 'BROUILLON', 'https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),

-- 4. Workshops & Hobbies (2026) -> AUTRE
(166, 'Digital Art Workshop', 'Création NFT et art numérique.', 'AUTRE', '2026-02-05 10:00:00', '2026-02-05 16:00:00', 'L''Uzine', 'Casablanca', 20, 400.00, 'PUBLIE', 'https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(167, 'Pottery Masterclass', 'Stage intensif de poterie.', 'AUTRE', '2026-04-12 09:00:00', '2026-04-14 17:00:00', 'Safi Artisanal Center', 'Safi', 15, 600.00, 'PUBLIE', 'https://images.unsplash.com/photo-1493106641515-6b5631de4bb9?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(168, 'Moroccan Cuisine Secrets', 'Atelier épices et tajines.', 'AUTRE', '2026-05-20 10:00:00', '2026-05-20 14:00:00', 'Riad Star', 'Fes', 12, 500.00, 'PUBLIE', 'https://images.unsplash.com/photo-1556910103-1c02745a30bf?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(169, 'Photography Walk Blue City', 'Stage photo à Chefchaouen.', 'AUTRE', '2026-03-15 08:00:00', '2026-03-16 18:00:00', 'Medina', 'Chefchaouen', 10, 800.00, 'PUBLIE', 'https://images.unsplash.com/photo-1526485856375-9110812fbf35?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(170, 'Arabic Calligraphy 2026', 'Initiation à la calligraphie.', 'AUTRE', '2026-01-25 14:00:00', '2026-01-25 17:00:00', 'Villa des Arts', 'Rabat', 25, 150.00, 'PUBLIE', 'https://images.unsplash.com/photo-1584448082985-802ba94a4af6?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),

-- 5. Food & Gastronomy (2026) -> AUTRE
(171, 'Casablanca Food Week', 'Festival culinaire international.', 'AUTRE', '2026-06-10 12:00:00', '2026-06-15 23:00:00', 'Anfa Park', 'Casablanca', 5000, 50.00, 'PUBLIE', 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(172, 'Chocolate Expo 2026', 'Salon du chocolat et de la pâtisserie.', 'AUTRE', '2026-02-14 10:00:00', '2026-02-16 19:00:00', 'Hyatt Regency', 'Casablanca', 1000, 80.00, 'PUBLIE', 'https://images.unsplash.com/photo-1511381939415-e44015466834?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(173, 'Wine Tasting Meknes', 'Découverte des vignobles.', 'AUTRE', '2026-09-20 11:00:00', '2026-09-20 16:00:00', 'Chateau Roslane', 'Meknes', 40, 600.00, 'BROUILLON', 'https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(174, 'Agadir Seafood Festival', 'Le meilleur des produits de la mer.', 'AUTRE', '2026-07-15 18:00:00', '2026-07-20 23:00:00', 'Port de Pêche', 'Agadir', 3000, 0.00, 'PUBLIE', 'https://images.unsplash.com/photo-1534939561126-855b8675edd7?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(175, 'Ramadan Ftour Deluxe', 'Ftour gastronomique caritatif.', 'AUTRE', '2026-03-20 18:30:00', '2026-03-20 21:00:00', 'Royal Mansour', 'Marrakech', 100, 1500.00, 'PUBLIE', 'https://images.unsplash.com/photo-1542587226-03730761d763?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),

-- 6. Professional & Real Estate (2026) -> CONFERENCE
(176, 'Smap Immo 2026', 'Salon de l''immobilier marocain.', 'CONFERENCE', '2026-05-05 09:00:00', '2026-05-08 19:00:00', 'Foire Internationale', 'Casablanca', 5000, 30.00, 'PUBLIE', 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(177, 'HR Summit Morocco', 'L''avenir des ressources humaines.', 'CONFERENCE', '2026-04-22 09:00:00', '2026-04-23 17:00:00', 'Sofitel Tour Blanche', 'Casablanca', 300, 900.00, 'PUBLIE', 'https://images.unsplash.com/photo-1515169067750-d51a743be79d?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(178, 'Agri-Tech Expo', 'Innovation en agriculture.', 'CONFERENCE', '2026-04-15 09:00:00', '2026-04-18 18:00:00', 'Bassin Agdal', 'Meknes', 2000, 50.00, 'PUBLIE', 'https://images.unsplash.com/photo-1495107334309-fcf20504a5ab?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(179, 'Freelance Connect', 'Networking pour indépendants.', 'CONFERENCE', '2026-01-30 18:00:00', '2026-01-30 21:00:00', 'Workspot', 'Rabat', 60, 0.00, 'ANNULE', 'https://images.unsplash.com/photo-1515187029135-18ee286d815b?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(180, 'Medical Expo 2026', 'Salon international de la santé.', 'CONFERENCE', '2026-06-25 09:00:00', '2026-06-28 18:00:00', 'OFEC', 'Casablanca', 1500, 100.00, 'PUBLIE', 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),

-- 7. Entertainment & Gaming (2026) -> AUTRE / CONCERT
(181, 'Morocco Gaming Fest', 'Finales E-Sport 2026.', 'AUTRE', '2026-07-10 10:00:00', '2026-07-12 20:00:00', 'Complexe Sportif', 'Rabat', 5000, 150.00, 'PUBLIE', 'https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(182, 'Cinema Under The Stars', 'Projections en plein air.', 'AUTRE', '2026-08-01 21:00:00', '2026-08-05 23:59:00', 'Jardin Majorelle', 'Marrakech', 200, 120.00, 'PUBLIE', 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(183, 'Summer Vibe Concert', 'DJ Set sur la plage.', 'CONCERT', '2026-08-15 16:00:00', '2026-08-16 02:00:00', 'Plage Martil', 'Tetouan', 2000, 200.00, 'PUBLIE', 'https://images.unsplash.com/photo-1459749411177-0473814a5530?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(184, 'Magic Show 2026', 'Spectacle de magie international.', 'THEATRE', '2026-05-12 19:00:00', '2026-05-12 21:00:00', 'Megarama', 'Casablanca', 600, 250.00, 'PUBLIE', 'https://images.unsplash.com/photo-1520697836932-5a41753952c4?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(185, 'Retro Car Show', 'Exposition voitures de collection.', 'AUTRE', '2026-04-20 10:00:00', '2026-04-22 18:00:00', 'California Golf', 'Casablanca', 500, 300.00, 'PUBLIE', 'https://images.unsplash.com/photo-1532581140115-3e355d1ed1de?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),

-- 8. Diverse & Regional (2026) -> AUTRE / CONCERT
(186, 'Tafraoute Almond Festival', 'Festival des Amandiers.', 'CONCERT', '2026-02-25 10:00:00', '2026-02-28 22:00:00', 'Place centrale', 'Tafraoute', 3000, 0.00, 'PUBLIE', 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(187, 'Tangier Fashion Week', 'Mode et créateurs du nord.', 'AUTRE', '2026-05-28 19:00:00', '2026-05-30 23:00:00', 'Hilton Tanger', 'Tanger', 300, 600.00, 'PUBLIE', 'https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(188, 'Errachidia Date Festival', 'Salon des dattes.', 'AUTRE', '2026-10-15 09:00:00', '2026-10-20 20:00:00', 'Place Hassan II', 'Errachidia', 4000, 0.00, 'BROUILLON', 'https://images.unsplash.com/photo-1603774619379-373dbd6a5061?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(189, 'Book Fair 2026', 'SIEL 2026 Rabat.', 'AUTRE', '2026-06-01 10:00:00', '2026-06-10 20:00:00', 'OLM Souissi', 'Rabat', 15000, 10.00, 'PUBLIE', 'https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(190, 'Auto Expo 2026', 'Nouveautés automobiles.', 'AUTRE', '2026-05-10 10:00:00', '2026-05-20 20:00:00', 'Foire Internationale', 'Casablanca', 8000, 50.00, 'PUBLIE', 'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),

-- 9. Final Mix (2026) -> AUTRE / SPORT
(191, 'Children Day Out', 'Parc d''attractions privatisé.', 'AUTRE', '2026-06-01 09:00:00', '2026-06-01 18:00:00', 'Palooza Land', 'Marrakech', 300, 100.00, 'PUBLIE', 'https://images.unsplash.com/photo-1472162072942-cd5147eb3902?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(192, 'Start-up Weekend Agadir', 'Hackathon innovation.', 'CONFERENCE', '2026-11-20 17:00:00', '2026-11-22 22:00:00', 'Universiapolis', 'Agadir', 100, 150.00, 'PUBLIE', 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(193, 'Art Deco Tour', 'Visite guidée architecture.', 'AUTRE', '2026-03-22 10:00:00', '2026-03-22 13:00:00', 'Bd Mohammed V', 'Casablanca', 30, 80.00, 'PUBLIE', 'https://images.unsplash.com/photo-1481277542470-60561ff950bc?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(194, 'Sufi Night 2026', 'Soirée spirituelle.', 'CONCERT', '2026-04-18 20:00:00', '2026-04-18 23:00:00', 'Musée Batha', 'Fes', 200, 200.00, 'PUBLIE', 'https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(195, 'Ultra Trail Atlas', 'Course extrême.', 'SPORT', '2026-05-15 00:00:00', '2026-05-16 18:00:00', 'Oukaimeden', 'Marrakech', 150, 800.00, 'PUBLIE', 'https://images.unsplash.com/photo-1533561052604-c3beb6d55760?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW()),
(196, 'Jazz au Chellah', 'Jazz dans les ruines.', 'CONCERT', '2026-09-10 19:00:00', '2026-09-14 23:00:00', 'Chellah', 'Rabat', 600, 150.00, 'PUBLIE', 'https://images.unsplash.com/photo-1511192336575-5a79af67a629?auto=format&fit=crop&w=800&q=80', 16, NOW(), NOW()),
(197, 'Bio Market 2026', 'Marché producteurs bio.', 'AUTRE', '2026-04-04 09:00:00', '2026-04-04 15:00:00', 'Bouskoura Forest', 'Casablanca', 1000, 0.00, 'PUBLIE', 'https://images.unsplash.com/photo-1488459716781-31db52582fe9?auto=format&fit=crop&w=800&q=80', 13, NOW(), NOW()),
(198, 'Crafts & Design', 'Salon du design artisanal.', 'AUTRE', '2026-10-01 10:00:00', '2026-10-05 19:00:00', 'Place El Hedim', 'Meknes', 2000, 20.00, 'PUBLIE', 'https://images.unsplash.com/photo-1595351298020-35398b2f0943?auto=format&fit=crop&w=800&q=80', 11, NOW(), NOW()),
(199, 'Summer Beach Volley', 'Tournoi amateur.', 'SPORT', '2026-08-08 10:00:00', '2026-08-08 18:00:00', 'Plage Ain Diab', 'Casablanca', 60, 50.00, 'BROUILLON', 'https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?auto=format&fit=crop&w=800&q=80', 9, NOW(), NOW()),
(200, 'New Year Eve 2027', 'Soirée du nouvel an.', 'CONCERT', '2026-12-31 20:00:00', '2027-01-01 04:00:00', 'Mazagan Resort', 'El Jadida', 1000, 2500.00, 'PUBLIE', 'https://images.unsplash.com/photo-1467810563316-b5476525c0f9?auto=format&fit=crop&w=800&q=80', 17, NOW(), NOW());

//Reservations
INSERT INTO RESERVATIONS (
    ID,
    CODE_RESERVATION,
    COMMENTAIRE,
    DATE_RESERVATION,
    MONTANT_TOTAL,
    NOMBRE_PLACES,
    STATUT,
    EVENT_ID,
    USER_ID
) VALUES
-- =============================================
-- CLIENT 8: Tech & Sports Fan (IDs 50-59)
-- =============================================
(50, 'RES-2025-001', 'Participation confirmée pour l''équipe.', '2025-05-10 09:30:00', 900.00, 3, 'CONFIRMEE', 101, 8), -- Tech Summit
(51, 'RES-2025-002', NULL, '2025-10-01 14:00:00', 200.00, 2, 'CONFIRMEE', 112, 8), -- Marathon
(52, 'RES-2025-003', 'En attente de visa.', '2025-09-01 10:00:00', 1200.00, 1, 'EN_ATTENTE', 104, 8), -- AI Conference
(53, 'RES-2025-004', 'Annulation cause maladie.', '2025-03-10 08:00:00', 1500.00, 1, 'ANNULEE', 105, 8), -- Cybersecurity
(54, 'RES-2026-005', 'Hâte de voir les nouveautés.', '2025-12-20 18:00:00', 350.00, 1, 'CONFIRMEE', 151, 8), -- AI Summit 2026
(55, 'RES-2026-006', NULL, '2025-12-28 11:00:00', 0.00, 1, 'EN_ATTENTE', 164, 8), -- Surf Pro (Free)
(56, 'RES-2025-007', 'Pour le département IT.', '2025-06-05 15:00:00', 1600.00, 2, 'CONFIRMEE', 102, 8), -- Marketing Masterclass
(57, 'RES-2026-008', NULL, '2025-12-30 09:00:00', 2000.00, 1, 'CONFIRMEE', 161, 8), -- Ironman
(58, 'RES-2025-009', 'Voyage d''affaires.', '2025-05-15 10:00:00', 300.00, 6, 'CONFIRMEE', 126, 8), -- Real Estate
(59, 'RES-2026-010', 'Early bird tickets.', '2025-12-15 14:00:00', 150.00, 1, 'CONFIRMEE', 154, 8), -- Cyber Defense

-- =============================================
-- CLIENT 10: Music & Festival Lover (IDs 60-69)
-- =============================================
(60, 'RES-2025-011', 'Trip entre amis.', '2025-06-15 12:00:00', 0.00, 5, 'CONFIRMEE', 106, 10), -- Gnaoua (Free)
(61, 'RES-2025-012', 'VIP Pass.', '2025-06-25 18:00:00', 700.00, 2, 'CONFIRMEE', 108, 10), -- Jazzablanca
(62, 'RES-2025-013', NULL, '2025-08-10 20:00:00', 60.00, 3, 'CONFIRMEE', 121, 10), -- Street Food
(63, 'RES-2025-014', 'Annulé cause transport.', '2025-05-28 10:00:00', 400.00, 1, 'ANNULEE', 107, 10), -- Fes Sacred Music
(64, 'RES-2026-015', 'Pré-commande.', '2025-12-01 09:00:00', 400.00, 2, 'CONFIRMEE', 156, 10), -- Mawazine
(65, 'RES-2026-016', 'Groupe de musique.', '2025-11-20 16:00:00', 400.00, 2, 'EN_ATTENTE', 183, 10), -- Summer Vibe
(66, 'RES-2025-017', NULL, '2025-07-01 14:00:00', 100.00, 2, 'CONFIRMEE', 119, 10), -- Marrakech Folklore
(67, 'RES-2026-018', 'Anniversaire de Sarah.', '2025-12-10 10:00:00', 300.00, 1, 'CONFIRMEE', 158, 10), -- Classical Music
(68, 'RES-2025-019', NULL, '2025-09-10 11:00:00', 3000.00, 2, 'CONFIRMEE', 110, 10), -- Oasis Festival
(69, 'RES-2026-020', 'Nouvel an !', '2025-12-31 08:00:00', 5000.00, 2, 'CONFIRMEE', 200, 10), -- NYE 2027

-- =============================================
-- CLIENT 12: Business Professional (IDs 70-79)
-- =============================================
(70, 'RES-2025-021', 'Stand B2B.', '2025-04-10 09:00:00', 50.00, 1, 'CONFIRMEE', 126, 12), -- Real Estate
(71, 'RES-2025-022', 'Délégation officielle.', '2025-09-15 08:30:00', 3000.00, 3, 'CONFIRMEE', 130, 12), -- Medical Congress
(72, 'RES-2025-023', NULL, '2025-11-01 10:00:00', 1000.00, 2, 'CONFIRMEE', 122, 12), -- Rabat Gourmet
(73, 'RES-2025-024', 'Ne pourra pas assister.', '2025-03-05 14:00:00', 100.00, 1, 'ANNULEE', 127, 12), -- Women in Tech
(74, 'RES-2026-025', 'Budget validé.', '2025-12-29 11:00:00', 900.00, 1, 'CONFIRMEE', 177, 12), -- HR Summit
(75, 'RES-2026-026', 'Sponsoring.', '2025-12-15 15:00:00', 500.00, 1, 'EN_ATTENTE', 153, 12), -- Green Tech
(76, 'RES-2025-027', NULL, '2025-04-20 09:00:00', 30.00, 1, 'CONFIRMEE', 138, 12), -- Agro Expo
(77, 'RES-2026-028', 'Formation continue.', '2025-12-10 09:00:00', 200.00, 1, 'CONFIRMEE', 152, 12), -- E-Com Days
(78, 'RES-2026-029', 'Networking.', '2025-12-05 18:00:00', 100.00, 1, 'CONFIRMEE', 155, 12), -- Startup Africa
(79, 'RES-2025-030', 'Invitation client.', '2025-12-10 20:00:00', 4000.00, 2, 'CONFIRMEE', 134, 12), -- Charity Gala

-- =============================================
-- CLIENT 14: General Interest / Mixed (IDs 80-89)
-- =============================================
(80, 'RES-2025-031', 'Startups locales.', '2025-04-15 19:00:00', 300.00, 2, 'CONFIRMEE', 103, 14), -- Startup Pitch
(81, 'RES-2025-032', 'Sortie famille.', '2025-05-01 10:00:00', 150.00, 3, 'ANNULEE', 140, 14), -- Auto Show
(82, 'RES-2025-033', NULL, '2025-06-01 10:00:00', 300.00, 2, 'CONFIRMEE', 141, 14), -- Kids Fun Day
(83, 'RES-2025-034', 'Gaming night.', '2025-07-10 10:00:00', 100.00, 1, 'CONFIRMEE', 133, 14), -- Gaming LAN
(84, 'RES-2026-035', 'Vacances hiver.', '2025-12-20 10:00:00', 150.00, 1, 'EN_ATTENTE', 163, 14), -- Atlas Race
(85, 'RES-2026-036', NULL, '2025-12-25 14:00:00', 300.00, 2, 'CONFIRMEE', 185, 14), -- Retro Car
(86, 'RES-2025-037', 'Cadeau.', '2025-08-15 10:00:00', 100.00, 2, 'CONFIRMEE', 149, 14), -- Beach Volley
(87, 'RES-2026-038', NULL, '2025-12-18 10:00:00', 150.00, 1, 'CONFIRMEE', 192, 14), -- Startup Agadir
(88, 'RES-2025-039', 'Films.', '2025-11-25 18:00:00', 400.00, 2, 'CONFIRMEE', 131, 14), -- Film Festival
(89, 'RES-2026-040', 'Amandes.', '2025-12-30 10:00:00', 0.00, 4, 'CONFIRMEE', 186, 14), -- Tafraoute Festival

-- =============================================
-- CLIENT 15: Arts, Culture & Workshops (IDs 90-99)
-- =============================================
(90, 'RES-2025-041', 'Atelier céramique.', '2025-03-30 15:00:00', 600.00, 2, 'CONFIRMEE', 117, 15), -- Pottery
(91, 'RES-2025-042', 'Fashion show.', '2025-11-10 12:00:00', 500.00, 1, 'CONFIRMEE', 137, 15), -- Fashion Week
(92, 'RES-2025-043', NULL, '2025-02-10 10:00:00', 20.00, 2, 'CONFIRMEE', 139, 15), -- Book Fair
(93, 'RES-2025-044', 'Cours annulé par l''org.', '2025-03-01 15:00:00', 200.00, 1, 'ANNULEE', 120, 15), -- Calligraphy
(94, 'RES-2026-045', 'Pour apprendre.', '2025-12-25 18:00:00', 150.00, 1, 'CONFIRMEE', 170, 15), -- Calligraphy 2026
(95, 'RES-2026-046', 'Miam chocolat.', '2025-12-31 08:00:00', 320.00, 4, 'EN_ATTENTE', 172, 15), -- Chocolate Expo
(96, 'RES-2026-047', 'Stage photo.', '2025-11-20 09:00:00', 800.00, 1, 'CONFIRMEE', 169, 15), -- Chefchaouen Photo
(97, 'RES-2025-048', NULL, '2025-05-20 10:00:00', 100.00, 2, 'CONFIRMEE', 116, 15), -- Tangier Art
(98, 'RES-2026-049', 'Design artisanal.', '2025-12-15 10:00:00', 40.00, 2, 'CONFIRMEE', 198, 15), -- Crafts Meknes
(99, 'RES-2026-050', 'Art numérique.', '2025-12-10 11:00:00', 400.00, 1, 'CONFIRMEE', 166, 15); -- Digital Art