package qliphongkham.fxmlNhanVien;

import Class.HangHoa;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.time.format.DateTimeFormatter;
import myUtil.DBconnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class ViewThanhToanController implements Initializable {

    @FXML
    private TableView<HangHoa> tablethanhtoan;
    @FXML
    private TableColumn<HangHoa, Integer> mahang2;
    @FXML
    private TableColumn<HangHoa, String> tenhang2;
    @FXML
    private TableColumn<HangHoa, Integer> soluong2;
    @FXML
    private TableColumn<HangHoa, Integer> giatien2;
    @FXML
    private TableColumn<HangHoa, Integer> thanhtien2;
    @FXML
    private Label nhapmakhachhang;
    @FXML
    private Label ngaytaikham;
    @FXML
    private Label ketluancuabacsi;
    @FXML
    private Label phanquanganh;
    @FXML
    private Label ngaytaohoadon;
    private String ketLuanCuabacsi;
    private String ngayTaoHoaDon;
    private int maKhachHang;
    private String tongTienkq;
    @FXML
    private Label hotenkh;
    @FXML
    private Label macuahd;
    @FXML
    private Label idHD;
    @FXML
    private Pane mainPanel;
    @FXML
    private Label sdtkh;

    @FXML
    private Label tongtien;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initMatHangTable();

            // Gắn sự kiện nhấp chuột cho nhãn "phanquanganh"
            phanquanganh.setOnMouseClicked((var event) -> {
                // Lấy giá trị từ nhãn "mã của hóa đơn"
                String macuahdValue = macuahd.getText();

                // Kiểm tra nếu giá trị không rỗng
                if (!macuahdValue.isEmpty()) {
                    // Chụp ảnh của giao diện
                    WritableImage image = mainPanel.snapshot(new SnapshotParameters(), null);
                    String folderPath = "D:\\HoaDon\\QuangAnh";
                    File folder = new File(folderPath);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    // Tạo tên file với định dạng "HD + nhapmakhachhangValue + .jpeg"
                    String fileName = "HD" + macuahdValue + ".png";
                    // Tạo đối tượng File từ đường dẫn và tên file
                    Path filePath = Paths.get(folderPath, fileName);
                    // Kiểm tra quyền ghi vào thư mục
                    if (Files.isWritable(filePath.getParent())) {
                        try {
                            // Định dạng ngày hiển thị
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                            // Chuyển đổi ngày từ định dạng hiển thị sang định dạng lưu trữ
                            LocalDate ngayLuuTru = LocalDate.parse(ngaytaikham.getText(), formatter);
                            String ngayLuuTruString = ngayLuuTru.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            luuThongTinHoaDon();
                            luuThongTinTaiKham(nhapmakhachhang.getText(), hotenkh.getText(), sdtkh.getText(), ngayLuuTruString, ketluancuabacsi.getText());

                            // Chuyển đổi và lưu ảnh dưới dạng file JPEG
                            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", filePath.toFile());

                            // Hiển thị thông báo "Đã lưu hóa đơn thành công"
                            showInformationAlert("Đã lưu hóa đơn thành công");

                            // Tạo một danh sách các giao diện đang mở
                            List<Stage> openStages = new ArrayList<>();

                            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            openStages.add(currentStage);
                            for (Stage stage : openStages) {
                                stage.close();
                            }

                            // Mở view "hoadon.fxml"
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("hoadon.fxml"));
                            Parent hoadonParent = loader.load();
                            Scene hoadonScene = new Scene(hoadonParent);
                            Stage hoadonStage = new Stage();
                            hoadonStage.setScene(hoadonScene);
                            hoadonStage.show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SQLException ex) {
                            Logger.getLogger(ViewThanhToanController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        // Thông báo cho người dùng rằng ứng dụng không có quyền ghi vào thư mục
                        showInformationAlert("Ứng dụng không có quyền ghi vào thư mục");
                    }

                }

            });
        } catch (SQLException ex) {
            Logger.getLogger(ViewThanhToanController.class.getName()).log(Level.SEVERE, null, ex);
        }
        int tong = tinhTongThanhTien();
        tongtien.setText(decimalFormat.format(tong));
    }

    private void showInformationAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void initMatHangTable() throws SQLException {
        mahang2.setCellValueFactory(cellData -> {
            HangHoa hangHoa = cellData.getValue();
            if (hangHoa.getIdThuoc() != 0) {
                return new SimpleObjectProperty<>(hangHoa.getIdThuoc());
            } else {
                return new SimpleObjectProperty<>(hangHoa.getIdDV());
            }
        });

        tenhang2.setCellValueFactory(cellData -> {
            HangHoa hangHoa = cellData.getValue();
            if (hangHoa.getIdThuoc() != 0) {
                return new SimpleStringProperty(hangHoa.getTenThuoc());
            } else {
                return new SimpleStringProperty(hangHoa.getTenDV());
            }
        });
        soluong2.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        soluong2.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        soluong2.setOnEditCommit(event -> {
            TablePosition<HangHoa, Integer> pos = event.getTablePosition();
            int newQuantity = event.getNewValue();
            HangHoa product = pos.getTableView().getItems().get(pos.getRow());
            product.setSoLuong(newQuantity);
            product.updateThanhTien();
            int tong = tinhTongThanhTien();
            tongtien.setText(decimalFormat.format(tong));
        });
        soluong2.setEditable(true);
        giatien2.setCellValueFactory(cellData -> {
            HangHoa hangHoa = cellData.getValue();
            if (hangHoa.getIdThuoc() != 0) {
                return new SimpleObjectProperty<>(hangHoa.getGiaThuoc());
            } else {
                return new SimpleObjectProperty<>(hangHoa.getGiaDV());
            }
        });

        giatien2.setCellFactory(column -> new TableCell<HangHoa, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(decimalFormat.format(item));
                }
            }
        });

        thanhtien2.setCellValueFactory(cellData -> {
            HangHoa hangHoa = cellData.getValue();
            return new SimpleObjectProperty<>(hangHoa.getThanhtien());
        });
        thanhtien2.setCellFactory(column -> new TableCell<HangHoa, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(decimalFormat.format(item));
                }
            }
        });

    }

    public void setIdHD(String id) {
        idHD.setText(id);
    }

    public void setSelectedProducts(ObservableList<HangHoa> selectedProducts) {
        tablethanhtoan.setItems(selectedProducts);
    }

    public void setNgayTaiKham(LocalDate ngayTaiKham) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String ngayTaiKhamFormatted = ngayTaiKham.format(formatter);
        ngaytaikham.setText(ngayTaiKhamFormatted);
    }

    public void setKetLuanCuabacsi(String ketLuanCuabacsi) {
        this.ketLuanCuabacsi = ketLuanCuabacsi;
        ketluancuabacsi.setText(ketLuanCuabacsi);
    }

    public void setNgayTaoHoaDon(String ngayTaoHoaDon) {
        this.ngayTaoHoaDon = ngayTaoHoaDon;
        ngaytaohoadon.setText(ngayTaoHoaDon);
    }

    public void setMaKhachHang(String maKhachHang) {
        try {
            int maKH = Integer.parseInt(maKhachHang);
            this.maKhachHang = maKH;
            nhapmakhachhang.setText(String.valueOf(maKH));
        } catch (NumberFormatException e) {
            // Xử lý lỗi khi không thể chuyển đổi giá trị thành số nguyên
            // Ví dụ: hiển thị thông báo lỗi
            showInformationAlert("Lỗi: Mã khách hàng phải là số nguyên");
        }
    }

    public void setNhapMaKhachHang(String maKhachHang) {
        try {
            int maKH = Integer.parseInt(maKhachHang);
            this.maKhachHang = maKH;
        } catch (NumberFormatException e) {
            // Xử lý lỗi khi không thể chuyển đổi giá trị thành số nguyên
            // Ví dụ: hiển thị thông báo lỗi
            showInformationAlert("Lỗi: Mã khách hàng phải là số nguyên");
        }
    }

    public void setTongTienkq(String tongTienkq) {
        this.tongTienkq = tongTienkq;
        tongtien.setText(tongTienkq);
    }

    public void generateMaCuaHD() {
        try {
            Connection connection = DBconnection.getConnect();
            String query = "SELECT MAX(idHD) AS max_id FROM hoadon";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int maxId = resultSet.getInt("max_id");
                int newId = maxId + 1;
                macuahd.setText(String.valueOf(newId));
            } else {
                macuahd.setText("1");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadKhachHangInfo() throws SQLException, IOException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = (Connection) DBconnection.getConnect();
            maKhachHang = Integer.parseInt(nhapmakhachhang.getText());

            String query = "SELECT hotenKH, sdtKH FROM khachhang WHERE idKH = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, maKhachHang);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String hotenKH = resultSet.getString("hotenKH");
                String sdtKH = resultSet.getString("sdtKH");

                // Hiển thị thông tin khách hàng trong hotenkhLabel và sdtkhLabel
                hotenkh.setText(hotenKH);
                sdtkh.setText(sdtKH);
            } else {
                showInformationAlert("Lỗi không thể tìm thấy tên và sđt khách hàng");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng kết nối, statement và resultSet
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public int tinhTongThanhTien() {
        int tong = 0;
        for (HangHoa product : tablethanhtoan.getItems()) {
            tong += product.getThanhtien();
        }
        return tong;
    }

    public void setTableThanhToanData(ObservableList<HangHoa> data) {
        tablethanhtoan.setItems(data);
    }

    public void luuThongTinHoaDon() {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBconnection.getConnect();

            // Lấy giá trị từ các trường tương ứng
            int maCuaHD = Integer.parseInt(macuahd.getText());
            int maKhachHang1 = Integer.parseInt(nhapmakhachhang.getText());
            String ngayTaoHoaDon1 = ngaytaohoadon.getText();
            int tongTien = tinhTongThanhTien(); // Phương thức tính tổng tiền

            // Thực hiện truy vấn để chèn dữ liệu vào bảng hoadon
            String query = "INSERT INTO hoadon (idHD, idKH, ngayMua, tongTien) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, maCuaHD);
            statement.setInt(2, maKhachHang1);
            statement.setString(3, ngayTaoHoaDon1);
            statement.setInt(4, tongTien);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void luuThongTinTaiKham(String maKH, String hotenKH, String sdtKH, String ngaytaikham, String ketluancuabacsi) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBconnection.getConnect();

            String query = "INSERT INTO lichtaikham (maKH, hotenKH, sdtKH, ngayTaiKham, ketLuanCuabacsi) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, maKH);
            statement.setString(2, hotenKH);
            statement.setString(3, sdtKH);
            statement.setString(4, ngaytaikham);
            statement.setString(5, ketluancuabacsi);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
