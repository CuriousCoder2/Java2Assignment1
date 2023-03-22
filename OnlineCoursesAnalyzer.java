
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class OnlineCoursesAnalyzer {
    public static class Course {
        private String institution;
        private int participants;

        public Course(String institution, int participants) {
            this.institution = institution;
            this.participants = participants;
        }

        public String getInstitution() {
            return institution;
        }

        public int getParticipants() {
            return participants;
        }
    }

    public static class Course4 {
        private String courseName;
        private int participants;
        private double hours;

        public Course4(String courseName, int participants, double hours) {
            this.courseName = courseName;
            this.participants = participants;
            this.hours = hours;
        }

        public String getCourseName() {
            return courseName;
        }

        public int getParticipants() {
            return participants;
        }

        public double getTotalHours() {
            return hours;
        }
    }

    public static class Course6 {
        private String courseNum;

        private String launchDate;
        private String courseName;
        private double averageUserAge;
        private double averageMaleRate;
        private double averageBachelorRate;

        public String getLaunchDate() {
            return launchDate;
        }

        public String getCourseName() {
            return courseName;
        }

        public Course6(String courseNum, String launchDate, String courseName, double averageUserAge, double averageMaleRate, double averageBachelorRate) {
            this.courseNum=courseNum;
            this.launchDate=launchDate;
            this.courseName = courseName;


            this.averageUserAge = averageUserAge;
            this.averageMaleRate = averageMaleRate;
            this.averageBachelorRate = averageBachelorRate;
        }

        public String getCourseNum() {
            return courseNum;
        }

        public double getAverageUserAge() {
            return averageUserAge;
        }

        public double getAverageMaleRate() {
            return averageMaleRate;
        }

        public double getAverageBachelorRate() {
            return averageBachelorRate;
        }
    }






    private List<String[]> data = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(datasetPath));
            br.readLine();
            String line;


            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                data.add(parts);


            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    //TODO：p1
    public Map<String, Integer> getPtcpCountByInst() {
        Stream<Course> courseStream = data.stream()
                .map(row -> new Course(row[0], Integer.parseInt(row[8])));
        return courseStream.collect(Collectors.groupingBy(Course::getInstitution,
                TreeMap::new, Collectors.summingInt(Course::getParticipants)));
    }

    //TODO：p2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Stream<Course> courseStream = data.stream()
                .map(row -> new Course(row[0] + "-" + (row[5].contains("\"") ? row[5].substring(1, row[5].length() - 1) : row[5]), Integer.parseInt(row[8])));
        return courseStream.collect(Collectors.groupingBy(Course::getInstitution,
                        TreeMap::new, // Use TreeMap for sorting
                        Collectors.summingInt(Course::getParticipants)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> instructorToCourseListMap= new HashMap<>();
        for (String[] row : data) {
            String[] instructors = (row[4].contains("\"") ? row[4].substring(1, row[4].length() - 1) : row[4]).split(","); // 获取讲师姓名列表



            String courseName = (row[3].contains("\"")?row[3].substring(1,row[3].length()-1):row[3]); // 获取课程名称
            if(instructors.length>1) {
                for (String instructor : instructors) {
                    String instructorName = instructor.trim();
                    List<List<String>>tmpList =new ArrayList<>();
                    tmpList.add(new ArrayList<>());
                    tmpList.add(new ArrayList<>());
                    instructorToCourseListMap.putIfAbsent(instructorName,tmpList);
                    instructorToCourseListMap.get(instructorName).get(1).add(courseName);

                }
            }
            else {
                List<List<String>>tmpList =new ArrayList<>();
                tmpList.add(new ArrayList<>());

                tmpList.add(new ArrayList<>());
                instructorToCourseListMap.putIfAbsent(instructors[0],tmpList);
                instructorToCourseListMap.get(instructors[0]).get(0).add(courseName);
            }
        }
        for ( Map.Entry<String, List<List<String>>> entry : instructorToCourseListMap.entrySet()) {
            String key=entry.getKey();

            List<List<String>>valuee=entry.getValue();
            Set<String> set = new LinkedHashSet<>(valuee.get(0));
            List<String>tmpzero = new ArrayList<>(set);
            Set<String> set2 = new LinkedHashSet<>(valuee.get(1));
            List<String>tmpone = new ArrayList<>(set2);



            Collections.sort(tmpzero);
            Collections.sort(tmpone);
            List<List<String>>tmppList =new ArrayList<>();
            tmppList.add(tmpzero);


            tmppList.add(tmpone);
            instructorToCourseListMap.put(key,tmppList);
        }
//        selfCourse.get()
//        otherCourse.get()

        return instructorToCourseListMap;
    }


    //TODO:p4
    public List<String> getCourses(int topK, String by) {
        Comparator<Course4> comparator;
        if (by.equals("hours")) {
            comparator = Comparator.comparing(Course4::getTotalHours).reversed().thenComparing(Comparator.comparing(Course4::getCourseName));
        } else if (by.equals("participants")) {
            comparator = Comparator.comparing(Course4::getParticipants).reversed().thenComparing(Comparator.comparing(Course4::getCourseName));
        } else {



            throw new IllegalArgumentException("Invalid argument for by: " + by);
        }

        List<String> sortedCourses = data.stream()
                .map(row -> new Course4(row[3], Integer.parseInt(row[8]), Double.parseDouble(row[17])))
                .sorted(comparator)
                .map(Course4::getCourseName).toList();

        List<String> result = new ArrayList<>();
        Set<String> addedCourseNames = new HashSet<>();

        for (String course : sortedCourses) {
            if (!addedCourseNames.contains(course)) {
                result.add(course);
                addedCourseNames.add(course);


            }
            if (result.size() == topK) {
                break;
            }
        }

        return result;
    }

    //TODO: p5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        List<String> courses = data.stream()
                .filter(row -> row[5].toLowerCase().contains(courseSubject.toLowerCase())
                        && Double.parseDouble(row[11]) >= percentAudited
                        && Double.parseDouble(row[17]) <= totalCourseHours)


                .map(row -> (row[3].contains("\"")?row[3].substring(1,row[3].length()-1):row[3]))        //同 p4，这个应该要返回的是名字而非课程代码
                .distinct()
                .sorted()
                .toList();

        return courses;
    }

    //TODO: p6


    private static class CourseSimilarity {
        private String courseNum;

        private double similarity;

        public CourseSimilarity(String courseNum, double similarity) {
            this.courseNum = courseNum;


            this.similarity = similarity;
        }

        public String getCourseNum() {
            return courseNum;
        }

        public double getSimilarity() {
            return similarity;
        }
    }

    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        List<Course6> course6s= data.stream().map(row -> new Course6(row[1], row[2], row[3], Double.parseDouble(row[19]), Double.parseDouble(row[20]), Double.parseDouble(row[22]))).toList();
        Map<String, List<Course6>> courseGroups = course6s.stream().collect(Collectors.groupingBy(Course6::getCourseNum));




        Map<String, Double> averageUserAge = courseGroups.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().collect(Collectors.averagingDouble(Course6::getAverageUserAge))
        ));

        Map<String, Double> averageMaleRate = courseGroups.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().collect(Collectors.averagingDouble(Course6::getAverageMaleRate))
        ));




        Map<String, Double> averageBachelorRate = courseGroups.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().collect(Collectors.averagingDouble(Course6::getAverageBachelorRate))
        ));

        List<CourseSimilarity> similarityList = new ArrayList<>();



        for (String courseNum : averageUserAge.keySet()) {
//            if (courseNum.contains("AT1x")) {
//                if(courseNum.contains("15.071x")){
            double avgAge = averageUserAge.get(courseNum);
            double avgGender = averageMaleRate.get(courseNum);
            double avgBachel = averageBachelorRate.get(courseNum);
            double similarity = Math.pow(avgAge - age, 2) + Math.pow(avgGender - gender * 100, 2) + Math.pow(avgBachel - isBachelorOrHigher * 100, 2);
            similarityList.add(new CourseSimilarity(courseNum, similarity));
//            }
        }
        similarityList.sort(Comparator.comparingDouble(CourseSimilarity::getSimilarity));



        List<String> topTenCourses = new ArrayList<>();
        for (int i = 0; i < 10 && i < similarityList.size(); i++) {
            List<Course6> ll=courseGroups.get(similarityList.get(i).getCourseNum());
            String courseNameA = ll.stream()
                    .min(Comparator.comparing(Course6::getLaunchDate))
                    .map(Course6::getCourseName)
                    .orElse(null);
            topTenCourses.add(courseNameA);

        }
        return topTenCourses;

    }
}