/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qliphongkham.fxmlNhanVien;

import Class.ChiTietHD;
import Class.ChiTietHDDV;
import Class.DichVu;
import Class.HangHoa;
import Class.HoaDon;
import Class.MatHang;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showInputDialog;
import myUtil.DBconnection;
import qliphongkham.fxmlChu.DichvuController;

public class HoadonController implements Initializable {

    @FXML
    private TextField seachThuocDV;
    @FXML
    private TableView<HangHoa> tablehienthi;
    @FXML
    private TableColumn<HangHoa, Integer> mahang1;
    @FXML
    private TableColumn<HangHoa, String> tenhang1;
    @FXML
    private TableColumn<HangHoa, Integer> giatien1;
    @FXML
    private Label tongtien;

    /**
     * Initializes the controller class.
     */
    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet rs = null;
    int soLuongHang;
    HoaDon hoaDon;
    HangHoa hangHoa7;
    ChiTietHD ctHD;
    ChiTietHDDV hddv;
    ArrayList<MatHang> mh;
    ArrayList<DichVu> dv;
    boolean checkHoaDon = false;
    boolean checkChonHang = true;
    boolean checkLichSuMua = false;
    boolean checkHoaDonDV = false;
    boolean checkChonDV = true;
    boolean checkLichSuDV = false;
    ObservableList<HangHoa> hanghoalist = FXCollections.observableArrayList();
    private Stage stage;
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
    private DatePicker ngaytaikham;
    @FXML
    private TextField nhapmakhachhang;
    @FXML
    private TextField ketluancuabacsi;

    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    TableColumn<HangHoa, HangHoa> xoa2 = new TableColumn<>("Xóa");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initMatHangTable();
            initThanhToanTable();
            tablethanhtoan.setRowFactory(tv -> {
                TableRow<HangHoa> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {
                        HangHoa selectedItem = row.getItem();
                        showInputDialog(selectedItem);
                    }
                });
                return row;
            });
        } catch (SQLException ex) {
            Logger.getLogger(HoadonController.class.getName()).log(Level.SEVERE, null, ex);
        }
        int tong = tinhTongThanhTien();
        tongtien.setText(decimalFormat.format(tong));
    }

    private void initMatHangTable() throws SQLException {
        connection = DBconnection.getConnect();
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

        tablehienthi.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                HangHoa selectedProduct = tablehienthi.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    int id = selectedProduct.getIdThuoc() != 0 ? selectedProduct.getIdThuoc() : selectedProduct.getIdDV();
                    String tenHang = selectedProduct.getIdThuoc() != 0 ? selectedProduct.getTenThuoc() : selectedProduct.getTenDV();
                    int giaTien = selectedProduct.getIdThuoc() != 0 ? selectedProduct.getGiaThuoc() : selectedProduct.getGiaDV();
                    boolean isProductExists = false;

                    for (HangHoa product : tablethanhtoan.getItems()) {
                        if ((product.getIdThuoc() == id || product.getIdDV() == id)
                                && (product.getTenThuoc().equals(tenHang) || product.getTenDV().equals(tenHang))) {
                            int newQuantity = product.getSoLuong() + 1;
                            product.setSoLuong(newQuantity);
                            product.updateThanhTien();
                            isProductExists = true;
                            break;
                        }
                    }

                    if (!isProductExists) {
                        HangHoa selectedProductToAdd = new HangHoa(id, tenHang, giaTien);
                        selectedProductToAdd.setSoLuong(1);
                        selectedProductToAdd.updateThanhTien();
                        tablethanhtoan.getItems().add(selectedProductToAdd);
                        if (!tablethanhtoan.getColumns().contains(xoa2)) {
                            tablethanhtoan.getColumns().add(xoa2);
                        }
                    }

                    tablethanhtoan.refresh();
                    int tong = tinhTongThanhTien();
                    tongtien.setText(decimalFormat.format(tong));
                }
            }
        });
    }

    private void initThanhToanTable() throws SQLException {
        connection = DBconnection.getConnect();
        xoa2.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        xoa2.setCellFactory(param -> new TableCell<HangHoa, HangHoa>() {
            private final Button deleteButton = new Button("Xóa");

            {
                deleteButton.setOnAction(event -> {
                    HangHoa product = getTableView().getItems().get(getIndex());
                    int thanhTien = product.getThanhtien();
                    int tongThanhTien = tinhTongThanhTien();
                    tongThanhTien = tongThanhTien - thanhTien;
                    tongtien.setText(decimalFormat.format(tongThanhTien));
                    getTableView().getItems().remove(product);
                    TableColumn<HangHoa, Integer> soluongColumn = soluong2;
                    if (soluongColumn != null) {
                        soluongColumn.setCellFactory(column -> new TableCell<HangHoa, Integer>() {
                            @Override
                            protected void updateItem(Integer quantity, boolean empty) {
                                super.updateItem(quantity, empty);
                                if (empty || quantity == null) {
                                    setText(null);
                                } else {
                                    setText(quantity.toString());
                                }
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(HangHoa product, boolean empty) {
                super.updateItem(product, empty);

                if (product == null || empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        tablethanhtoan.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                HangHoa selectedProduct = tablethanhtoan.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                }
                int tong = tinhTongThanhTien();
                tongtien.setText(decimalFormat.format(tong));
            }
        });
        tablethanhtoan.getColumns().add(xoa2);
    }

    private void showInputDialog(HangHoa selectedItem) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nhập số lượng");
        dialog.setHeaderText(null);
        dialog.setContentText("Nhập số lượng:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            try {
                int newQuantity = Integer.parseInt(quantity);
                selectedItem.setSoLuong(newQuantity);
                selectedItem.updateThanhTien();
                int tong = tinhTongThanhTien();
                tongtien.setText(decimalFormat.format(tong));
                tablethanhtoan.refresh();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Số lượng phải là một số nguyên.");
                alert.showAndWait();
            }
        });
    }

    public ObservableList<HangHoa> copyDataFromTableThanhToan() {
        ObservableList<HangHoa> data = tablethanhtoan.getItems();
        ObservableList<HangHoa> copiedData = FXCollections.observableArrayList(data);
        return copiedData;
    }

    @FXML
    private void handleButtonThanhToan(MouseEvent event) throws IOException, SQLException {
        // Lấy danh sách các sản phẩm đã chọn từ tablethanhtoan
        ObservableList<HangHoa> selectedProducts = tablethanhtoan.getItems();
        String ketLuanCuabacsi = ketluancuabacsi.getText();
        String ngayTaoHoaDon = getFormattedCurrentDateTime();
        String maKhachHang = getMaKhachHang();
        String tongTienkq = tongtien.getText();

        // Truyền danh sách sản phẩm đã chọn cho viewthanhtoan.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("viewthanhtoan.fxml"));
        Parent fxml = loader.load();
        ViewThanhToanController thanhToanController = loader.getController();
        thanhToanController.setSelectedProducts(selectedProducts);
        thanhToanController.setNgayTaiKham(ngaytaikham.getValue());
        thanhToanController.setKetLuanCuabacsi(ketLuanCuabacsi);
        thanhToanController.setNgayTaoHoaDon(ngayTaoHoaDon);
        thanhToanController.setMaKhachHang(maKhachHang);
        thanhToanController.setNhapMaKhachHang(maKhachHang);

        thanhToanController.loadKhachHangInfo();
        thanhToanController.generateMaCuaHD();

        ObservableList<HangHoa> copiedData = copyDataFromTableThanhToan();
        thanhToanController.setTableThanhToanData(copiedData);
        thanhToanController.setTongTienkq(tongTienkq);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        // Hiển thị viewthanhtoan.fxml
        Scene scene = new Scene(fxml);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Hóa Đơn");
        stage.setScene(scene);
        stage.show();
    }

    private List<String> validateInputs() {
        List<String> errors = new ArrayList<>();
        String makhachhang = nhapmakhachhang.getText();
        if (makhachhang.isEmpty()) {
            errors.add("Vui lòng nhập mã khách hàng.");
        }
        String klcuabacsi = ketluancuabacsi.getText();
        if (klcuabacsi.isEmpty()) {
            errors.add("Thiếu kết luận của bác sĩ");
        }
        LocalDate ngayTaiKham = ngaytaikham.getValue();
        if (ngayTaiKham == null) {
            errors.add("Vui lòng chọn ngày tái khám.");
        }
        if (tablethanhtoan.getItems().isEmpty()) {
            errors.add("Không có hàng hóa nào để thanh toán.");
        }
        return errors;
    }

    private String getFormattedCurrentDateTime() {
        // Lấy thời gian hiện tại
        LocalDateTime currentTime = LocalDateTime.now();

        // Định dạng thời gian theo kiểu "dd-MM-yyyy HH:mm"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = currentTime.format(formatter);

        return formattedDateTime;
    }

    private String getMaKhachHang() {
        return nhapmakhachhang.getText();
    }

    private int tinhTongThanhTien() {
        int tong = 0;
        for (HangHoa product : tablethanhtoan.getItems()) {
            tong += product.getThanhtien();
        }
        return tong;
    }

    private void updateTongTien() {
        try {
            int tongTien = ctHD.getTongTien(mh.size());
            int maHD = ctHD.getHoaDon().getMaHD();
            connection = DBconnection.getConnect();
            connection.createStatement().executeUpdate("update hoadon set tongtien='" + tongTien + "' where idHD=" + maHD + ";");
            congDiem(tongTien);
        } catch (SQLException ex) {
            Logger.getLogger(HoadonController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateTongTienDV() {
        try {
            int tongTien = hddv.getTongTien();
            System.out.println(tongTien);
            int maHD = hddv.getHoaDon().getMaHD();
            connection = DBconnection.getConnect();
            connection.createStatement().executeUpdate("update hoadon set tongtien='" + tongTien + "' where idHD=" + maHD + ";");
            congDiemDV(tongTien);
        } catch (SQLException ex) {
            Logger.getLogger(HoadonController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void congDiem(int tongTien) throws SQLException {
        int diemKM = 0;
        int tongDiem = 0;
        int maKh = ctHD.getHoaDon().getMaKH();
        connection = DBconnection.getConnect();
        rs = connection.createStatement().executeQuery("select diemKM from khachhang where idKH=" + maKh);
        while (rs.next()) {
            diemKM = rs.getInt("diemKM");
        }

        if (tongTien < 100000) {
            tongDiem = 1 + diemKM;
        } else if (tongTien > 100000) {
            tongDiem = 2 + diemKM;
        } else if (tongTien > 500000) {
            tongDiem = 10 + diemKM;
        } else {
            tongDiem = 20 + diemKM;
        }
        connection.createStatement().executeUpdate("update khachhang set diemKM='" + tongDiem + "' where idKH=" + maKh + ";");
        connection.close();
    }

    private void congDiemDV(int tongTien) throws SQLException {
        int diemKM = 0;
        int tongDiem = 0;
        int maKh = hddv.getHoaDon().getMaKH();
        connection = DBconnection.getConnect();
        rs = connection.createStatement().executeQuery("select diemKM from khachhang where idKH=" + maKh);
        while (rs.next()) {
            diemKM = rs.getInt("diemKM");
        }

        if (tongTien < 100000) {
            tongDiem = 1 + diemKM;
        } else if (tongTien > 100000) {
            tongDiem = 2 + diemKM;
        } else if (tongTien > 500000) {
            tongDiem = 10 + diemKM;
        } else {
            tongDiem = 20 + diemKM;
        }
        connection.createStatement().executeUpdate("update khachhang set diemKM='" + tongDiem + "' where idKH=" + maKh + ";");
        connection.close();
    }

    private void updateThuocConLai(int maThuoc, int soThuocCon) throws SQLException {
        try {
            connection = DBconnection.getConnect();
            connection.createStatement().executeUpdate("update thuoc set soluong='" + soThuocCon + "' where idThuoc=" + maThuoc + ";");
            tablehienthi.getItems().clear();
            initMatHangTable();
        } catch (SQLException ex) {
            Logger.getLogger(HoadonController.class.getName()).log(Level.SEVERE, null, ex);
        }
        connection.close();
    }

    private void insertHD(int maNV, Integer maKH, String ngayMua, int tongTien) {
        try {
            connection = DBconnection.getConnect();
            preparedStatement = connection.prepareStatement("INSERT INTO hoadon(idNV,idKH,ngaymua,tongtien) VALUES (?,?,?,?);");
            preparedStatement.setInt(1, maNV);
            preparedStatement.setInt(2, maKH);
            preparedStatement.setString(3, ngayMua);
            preparedStatement.setInt(4, tongTien);
            preparedStatement.execute();

//            Stage thisStage = (Stage) tfTenThuoc.getScene().getWindow();
//            thisStage.close();
        } catch (SQLException ex) {
            Logger.getLogger(HoadonController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getMaHD(int maNV, Integer maKH, String ngayMua, int tongTien) throws SQLException {
        int maHD = 0;
        if (maKH != null) {
            rs = connection.createStatement().executeQuery("select idHD from hoadon where idNV=" + maNV + " and idKH=" + maKH + " and ngaymua='" + ngayMua + "' and tongtien=" + tongTien + ";");
        } else {
            rs = connection.createStatement().executeQuery("select idHD from hoadon where idNV=" + maNV + " and idKH is null and ngaymua='" + ngayMua + "' and tongtien=" + tongTien + ";");
        }
        if (rs.next()) {
            maHD = rs.getInt("idHD");
        }
        return maHD;
    }

    @FXML
    private void evseachThuocDV(KeyEvent event) throws SQLException {
        if (event.getCode() == KeyCode.ENTER) { // Kiểm tra nếu phím được ấn là phím Enter
            String searchWord = seachThuocDV.getText();
            tablehienthi.getItems().clear();
            ObservableList<HangHoa> searchThuocList = FXCollections.observableArrayList();
            connection = DBconnection.getConnect();
            try {
                rs = connection.createStatement().executeQuery("SELECT * FROM dichvu WHERE tenDV LIKE '%" + searchWord + "%' OR idDV LIKE '%" + searchWord + "%'");

                while (rs.next()) {
                    searchThuocList.add(new HangHoa(rs.getInt("idDV"), rs.getString("tenDV"), rs.getInt("giaDV")));
                }
            } catch (SQLException ex) {
                Logger.getLogger(DichvuController.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs = connection.createStatement().executeQuery("SELECT * FROM thuoc WHERE tenThuoc LIKE '%" + searchWord + "%' OR idThuoc LIKE '%" + searchWord + "%'");

                while (rs.next()) {
                    searchThuocList.add(new HangHoa(rs.getInt("idThuoc"), rs.getString("tenThuoc"), rs.getInt("giaThuoc")));
                }
            } catch (SQLException ex) {
                Logger.getLogger(HoadonController.class.getName()).log(Level.SEVERE, null, ex);
            }
            mahang1.setCellValueFactory((TableColumn.CellDataFeatures<HangHoa, Integer> param) -> {
                if (param.getValue().getIdThuoc() != 0) {
                    return new SimpleIntegerProperty(param.getValue().getIdThuoc()).asObject();
                } else {
                    return new SimpleIntegerProperty(param.getValue().getIdDV()).asObject();
                }
            });

            tenhang1.setCellValueFactory((TableColumn.CellDataFeatures<HangHoa, String> param) -> {
                if (param.getValue().getIdThuoc() != 0) {
                    return new SimpleStringProperty(param.getValue().getTenThuoc());
                } else {
                    return new SimpleStringProperty(param.getValue().getTenDV());
                }
            });

            giatien1.setCellValueFactory((TableColumn.CellDataFeatures<HangHoa, Integer> param) -> {
                if (param.getValue().getIdThuoc() != 0) {
                    return new SimpleIntegerProperty(param.getValue().getGiaThuoc()).asObject();
                } else {
                    return new SimpleIntegerProperty(param.getValue().getGiaDV()).asObject();
                }
            });
            tablehienthi.setItems(searchThuocList);
            connection.close();
        }
    }

//    @FXML
//    private void reloadHDEvent(MouseEvent event) throws SQLException {
//        HoaDonTable.getItems().clear();
//        initHoaDon();
//    }
    @FXML
    private void xemChiTietHoaDon(MouseEvent event) throws IOException {
        if (checkHoaDon == true) {
            Parent fxml = FXMLLoader.load(getClass().getResource("view_ChiTietHD.fxml"));
            Scene scene = new Scene(fxml);
            Stage stage1 = new Stage();
            stage1.initStyle(StageStyle.UTILITY);
            stage1.setTitle("Xem dữ liệu");
            stage1.setScene(scene);
            stage1.show();
            stage1.setUserData(ctHD);
            System.out.println(checkLichSuMua);
            if (checkLichSuMua == false) {

                addLichSuMua();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Không có hóa đơn để xem!", "Lỗi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addLichSuMua() {
        checkLichSuMua = true;
        Integer maKH = ctHD.getHoaDon().getMaKH();
        String ngayMua = ctHD.getHoaDon().getNgayMua();
        int maHD = ctHD.getHoaDon().getMaHD();
        String thuocMua = "";
        for (MatHang i : mh) {
            thuocMua = thuocMua + i.getThuoc().getTenThuoc() + ", ";
        }

        try {
            connection = DBconnection.getConnect();
            preparedStatement = connection.prepareStatement("INSERT INTO khmuathuoc(idKH,ngaymua,thuocmua,idHD) VALUES (?,?,?,?);");
            preparedStatement.setInt(1, maKH);
            preparedStatement.setString(2, ngayMua);
            preparedStatement.setString(3, thuocMua);
            preparedStatement.setInt(4, maHD);
            preparedStatement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(HoadonController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addLichSuDV() {
        checkLichSuDV = true;
        Integer maKH = hddv.getHoaDon().getMaKH();
        String ngayMua = hddv.getHoaDon().getNgayMua();
        int maHD = hddv.getHoaDon().getMaHD();

        String dvTT = "";
        System.out.println("da them LS Dv");
        for (DichVu i : dv) {
            dvTT = dvTT + i.getTenDV() + ", ";
        }

        try {
            connection = DBconnection.getConnect();
            preparedStatement = connection.prepareStatement("INSERT INTO khkhambenh(idKH,ngaykham,noidungkham,idHD) VALUES (?,?,?,?);");
            preparedStatement.setInt(1, maKH);
            preparedStatement.setString(2, ngayMua);
            preparedStatement.setString(3, dvTT);
            preparedStatement.setInt(4, maHD);
            preparedStatement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(HoadonController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void xemChiTietHoaDonDV(MouseEvent event) throws IOException {
        if (checkHoaDonDV == true) {
            Parent fxml = FXMLLoader.load(getClass().getResource("view_ChiTietDV.fxml"));
            Scene scene = new Scene(fxml);
            Stage stage2 = new Stage();
            stage2.initStyle(StageStyle.UTILITY);
            stage2.setTitle("Xem dữ liệu");
            stage2.setScene(scene);
            stage2.show();
            stage2.setUserData(hddv);
            if (checkLichSuDV == false) {
                addLichSuDV();
            }

        } else {
            JOptionPane.showMessageDialog(null, "Không có hóa đơn để xem!", "Lỗi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
